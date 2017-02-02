package com.levi9.controller.api

import akka.http.scaladsl.server.Directives._
import com.levi9.dao.Query.QuerySourceTypeDao
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global

trait SourceTypeQueryController extends JsonSupport {

  val sourceTypeDao: QuerySourceTypeDao

  def sourceType() = (path("source" / "type") & get & pathEnd) (complete(sourceTypeDao.findAll().map(_.toJson)))
}
