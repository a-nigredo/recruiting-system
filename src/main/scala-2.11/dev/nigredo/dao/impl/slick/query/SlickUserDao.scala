package com.levi9.dao.impl.slick.query

import com.levi9._
import com.levi9.dao.filter.Filter
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.impl.slick.common.tables.{UserTable, VocabularyTable}
import com.levi9.dao.Query.{PageableUserDao, UserDao}
import com.levi9.dao.impl.slick.query.filter.BasicInterpreter
import com.levi9.dao.{Page, Pageable}
import com.levi9.projection.{Location, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.Scalaz._

protected[slick] trait SlickUserDao
  extends UserTable
    with JDBCDataSourceProvider {
  parent =>

  import SlickRoleDao._
  import profile.api._

  val filterInterpreter: BasicInterpreter[(UserTable#User, VocabularyTable#Vocabularies)]

  val userDao = new UserDaoImpl {}
  val pageableUserDao = new PageableUserDaoImpl {}

  protected[slick] trait UserDaoImpl extends UserDao {

    override def findAll(filter: Option[Filter]) = {
      val query = for {
        q <- filter.map(baseQuery).getOrElse(q)
      } yield q
      source.run(query.result).map(_.seq.toList.map(user => User(user._1.id.get, user._1.name, user._1.email, user._2, Location(user._1.location))))
    }

    override def findOne(filter: Filter) = parent.findOne(filter)
  }

  protected[slick] trait PageableUserDaoImpl extends PageableUserDao {

    override def findAll(filter: Option[Filter]) = (page: Pageable) => (for {
      total <- source.run(users.size.result)
      range = page.rangeFor(total)
      result <- source.run(filter.map(baseQuery).getOrElse(q).drop(range.from).take(page.size).sortBy(_._1.id).result)
    } yield result).map(x => Page(x.seq.toList.map(user => User(user._1.id.get, user._1.name, user._1.email, user._2, Location(user._1.location))), x.size))

    override def findOne(filter: Filter) = parent.findOne(filter)
  }

  private def findOne(filter: Filter) = {
    val query = for {
      result <- baseQuery(filter)
    } yield result
    source.run(query.result.headOption)
      .mapF(x => User(x._1.id.get, x._1.name, x._1.email, x._2, Location(x._1.location)))
  }

  private lazy val q = users join roles on (_.role === _.id)

  private def baseQuery(filter: Filter) = q.filter(x => filterInterpreter.interpret(filter, x))

}