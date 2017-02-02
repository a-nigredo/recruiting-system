package com.levi9.controller.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import spray.json.DefaultJsonProtocol

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  case class ErrorJson(message: String)

  implicit val errorFormat = jsonFormat1(ErrorJson)

  def Ok(content: String = "") = complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, content)))

  def BadRequest(errors: List[ErrorJson]) = complete(HttpResponse(status = StatusCodes.BadRequest,
    entity = HttpEntity(ContentTypes.`application/json`, errors.toJson.toString)))

  def Created(value: String = "") = complete(HttpResponse(status = StatusCodes.Created, entity = HttpEntity(ContentTypes.`application/json`, value)))

  def InternalServerError(errors: List[ErrorJson]) = complete(HttpResponse(status = StatusCodes.InternalServerError,
    entity = HttpEntity(ContentTypes.`application/json`, errors.toJson.toString)))

  def NotFound = complete(HttpResponse(StatusCodes.NotFound))

  def Unauthorized = complete(HttpResponse(StatusCodes.Unauthorized))

  def Forbidden = complete(HttpResponse(StatusCodes.Forbidden))
}
