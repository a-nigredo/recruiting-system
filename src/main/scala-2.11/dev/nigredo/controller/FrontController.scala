package com.levi9.controller

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, _}
import com.levi9._
import com.levi9.controller.api.JsonSupport
import com.levi9.projection.User
import com.levi9.service.{AuthenticationService, AuthorizationService}

trait FrontController
  extends SimpleController
    with JsonSupport {

  val authenticationService: AuthenticationService
  val authorizationService: AuthorizationService
  val loginController: Controller
  val resourceController: Controller
  val controller: Set[Controller]

  private val securedController = controller.filter(x => x.isInstanceOf[SecuredController]).map(_.routes())
  private val notSecuredController = controller.filter(x => !x.isInstanceOf[SecuredController]).map(_.routes())

  protected def buildRoutes() = {

    def authenticated: Directive1[User] = headerValueByName("X-Auth-Token").flatMap { token =>
      onSuccess(authenticationService.authenticate(token)).flatMap {
        case Some(t) => provide(t)
        case None => Unauthorized
      }
    }

    def securedRoute: Option[Route] = if (securedController.nonEmpty) {
      Some(Route.seal {
        (authenticated & extractRequest) { (user, request) =>
          val isAuthorized = authorizationService.authorize(user.role.name, request.method.name, request.uri.path.toString(), user.location)
          onSuccess(isAuthorized) { result =>
            if (result) securedController.map(f => f.applyOrElse(user, f)).reduce(_ ~ _)
            else Forbidden
          }
        } ~ loginController.routes()(())
      })
    } else None

    def notSecured: Option[Route] = if (notSecuredController.nonEmpty)
      Some(notSecuredController.map(_ (())).reduce(_ ~ _))
    else None

    pathPrefix("api") {
      respondWithHeaders(RawHeader("Access-Control-Allow-Origin", "*"),
        RawHeader("Access-Control-Allow-Headers", "X-Auth-Token, Content-Type"),
        RawHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT")) {
        val option = options(complete(""))
        val secured = securedRoute.fold(option)(option ~ _)
        notSecured.fold(secured)(_ ~ secured)
      }
    } ~ resourceController.routes()(())
  }
}