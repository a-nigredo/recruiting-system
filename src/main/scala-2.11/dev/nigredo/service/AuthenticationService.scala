package com.levi9.service

import java.time.Instant
import java.util.UUID

import com.levi9._
import com.levi9.dao.Command.CommandAccessTokenDao
import com.levi9.dao.Query.{QueryAccessTokenDao, QueryRoleDao}
import com.levi9.dao.filter.dsl.AccessToken.{AccessTokenUserId, AccessTokenValue}
import com.levi9.dao.filter.dsl.Role.RoleName
import com.levi9.dao.filter.dsl.User.{UserEmail, UserId}
import com.levi9.dao.{Command, Query}
import com.levi9.domain.model.Security.AccessToken
import com.levi9.domain.model.Users.User
import com.levi9.projection.Location
import com.typesafe.scalalogging.Logger
import com.unboundid.ldap.sdk._
import org.slf4j.LoggerFactory

import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scalaz.Scalaz._
import scalaz.{Id => _, _}

trait AuthenticationService {

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  import scala.collection.JavaConversions._

  type AccessTokenDao = CommandAccessTokenDao with QueryAccessTokenDao
  type CommandUserDao = Command.CommandUserDao
  type QueryUserDao = Query.QueryUserDao
  type Auth = Either[Boolean, Future[Authentication]]
  type RoleDao = QueryRoleDao
  type ConfigDao = Query.QueryConfigDao

  val accessTokenDao: AccessTokenDao
  val commandUserDao: CommandUserDao
  val queryUserDao: QueryUserDao
  val roleDao: RoleDao
  val configDao: ConfigDao

  private val tokenLifeTime = TokenTime(Duration(config.getString("security.tokenLifeTime")))

  case class TokenTime(lifeTime: Duration) {
    def expireTime = Instant.now().plusMillis(lifeTime.toMillis).toEpochMilli
  }

  case class Authentication(token: String, user: projection.User)

  case class Credentials(login: String, password: String)

  case class LdapParams(hosts: List[String], port: Integer, domain: String, baseDn: String, location: Location)

  def authenticate(token: String) = {
    val lifeTime = tokenLifeTime.lifeTime.toMillis
    accessTokenDao.findOne(AccessTokenValue == token).flatMap {
      case Some(value) if value.isExpired(lifeTime) =>
        accessTokenDao.removeToken(value.value)
        Future.successful(None)
      case Some(value) => for {
        _ <- accessTokenDao.prolongExpireDate(token, tokenLifeTime.expireTime)
        result <- queryUserDao.findOne(UserId == value.user)
      } yield result
      case None => Future.successful(None)
    }
  }

  private val defaultLdapParams = officesConfig.map { x =>
    val ldapParams = x.getConfig("ldap")
    LdapParams(ldapParams.getStringList("host").toList, ldapParams.getInt("port"), ldapParams.getString("domain"),
      ldapParams.getString("baseDn"), Location(x.getString("location")))
  }.toList

  def login(credentials: Credentials, ldapParams: List[LdapParams] = defaultLdapParams): Auth = {

    @tailrec
    def tryLogin(ldapParams: LdapParams, credentials: Credentials, result: Auth = Left(false)): Auth = {
      val host = ldapParams.hosts
      val location = ldapParams.location
      if (result.isRight || host.isEmpty) result
      else {
        tryLogin(ldapParams.copy(hosts = ldapParams.hosts.tail), credentials, {
          val ldap = new LDAPConnection(host.head, ldapParams.port)
          val email = s"${credentials.login}@${ldapParams.domain}"
          try {
            if (ldap.login(email, credentials.password))
              Right(ldap.search(ldapParams.baseDn, SearchScope.SUB, s"(mail=$email)", "cn", "mail", "department").getToken(location))
            else Left(false)
          } catch {
            case e: LDAPException =>
              logger.error(s"Unable to login '$email': $e")
              Left(false)
          } finally {
            ldap.close()
          }
        })
      }
    }

    ldapParams.collectFirst {
      case ldapparam => tryLogin(ldapparam, credentials)
    }.get
  }

  implicit class RichLdapConnection(conn: LDAPConnection) {

    def login(login: String, password: String): Boolean = conn.bind(new SimpleBindRequest(login, password))
      .getResultCode.equals(ResultCode.SUCCESS)
  }

  implicit class RichSearchResult(searchResult: SearchResult) {

    def getToken(location: Location) = {

      def create(entry: SearchResultEntry) = {
        val name = entry.getAttribute("cn").getValue
        val email = entry.getAttribute("mail").getValue
        val department = entry.getAttribute("department").getValue

        def md5Hash(text: String): String = java.security.MessageDigest.getInstance("MD5").digest(text.getBytes())
          .map(0xFF & _)
          .map {
            "%02x".format(_)
          }
          .foldLeft("") {
            _ + _
          }

        def createAccessToken(user: projection.User) = for {
          _ <- accessTokenDao.findOne(AccessTokenUserId == user.id).mapF(x => accessTokenDao.removeToken(x.value))
          token = AccessToken(md5Hash(UUID.randomUUID().toString), tokenLifeTime.expireTime, user.id)
          _ <- accessTokenDao.save(token)
        } yield token

        def createUser = OptionT(queryUserDao.findOne(UserEmail == email)).getOrElseF {
          val security = configDao(location).security
          val roleName = security.roles.find(_.equalsIgnoreCase(department)).getOrElse(security.defaultRole)
          for {
            role <- roleDao.findOne(RoleName == roleName)
            userId <- commandUserDao.save(User(name = name, email = email, role = role.map(_.id).get, location = location.id))
          } yield projection.User(userId, name, email, role.get, location)
        }

        for {
          user <- createUser
          token <- createAccessToken(user)
        } yield Authentication(token.value, user)
      }

      searchResult.getSearchEntries.map(create).head
    }
  }

}
