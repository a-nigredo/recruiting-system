package com.levi9.controller

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.levi9.common.{NotFoundError, SystemError, ValidationError}
import com.levi9.controller.api.JsonSupport
import com.levi9.projection.User

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

sealed trait Controller extends JsonSupport {

  def routes(): PartialFunction[Any, Route]

  protected def respond[A](input: Try[Future[A]])(body: => A => Route): Route = input match {
    case Success(value) => respond(value)(body)
    case f@Failure(_) => failure(f)
  }

  protected def respond[A](input: Future[A])(body: => A => Route) = onComplete(input) {
    case Success(value) => body(value)
    case f@Failure(_) => failure(f)
  }

  private def failure(failure: Failure[_]) = failure match {
    case Failure(error: ValidationError) => BadRequest(error.messages.map(ErrorJson))
    case Failure(error: NotFoundError) => NotFound
    case Failure(error: SystemError) => InternalServerError(List(ErrorJson(error.ex.getMessage)))
    case Failure(error: Throwable) => InternalServerError(List(ErrorJson(error.getMessage)))
  }

  val isCommand = post | put
  val isQuery = get
}

trait SimpleController extends Controller {

  protected def buildRoutes(): Route

  override def routes() = {
    case _ => buildRoutes()
  }
}

trait UserAwareController extends Controller with SecuredController {

  protected def buildRoutes(user: User): Route

  override def routes() = {
    case user: User => buildRoutes(user)
  }
}

trait SecuredController