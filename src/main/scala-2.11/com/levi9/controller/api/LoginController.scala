package com.levi9.controller.api

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RejectionHandler, UnsupportedRequestContentTypeRejection}
import com.levi9.controller.SimpleController
import com.levi9.dao.Query.QueryConfigDao
import com.levi9.projection.Auth
import com.levi9.service.AuthenticationService
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait LoginController
  extends JsonSupport
    with SimpleController {

  type AuthServiceType = AuthenticationService

  val authService: AuthServiceType
  val configDao: QueryConfigDao

 implicit val loginFormat = jsonFormat2(authService.Credentials)

  private val wrongMediaTypeRejectionHandler = RejectionHandler.newBuilder().handle {
    case UnsupportedRequestContentTypeRejection(value) => complete(StatusCodes.UnsupportedMediaType,
      s"Supports: ${value.mkString(",")}")
  }.result()

  override def buildRoutes() = handleRejections(wrongMediaTypeRejectionHandler) {

    implicit val formats = Serialization.formats(NoTypeHints)

    (path("login") & post & entity(as[authService.Credentials]) & pathEnd) { credentials =>
      authService.login(credentials).fold({ _ => Unauthorized }, { result =>
        val response = for {
          auth <- result
          acl <- Future.successful(configDao(auth.user.location).security.acl.find(_.role.equalsIgnoreCase(auth.user.role.name)))
        } yield Auth(auth.token, auth.user.name, acl)
        onSuccess(response)(x => Ok(write(x)))
      })
    }
  }

}