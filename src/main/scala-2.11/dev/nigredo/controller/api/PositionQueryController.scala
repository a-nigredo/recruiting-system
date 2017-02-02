package com.levi9.controller.api

import akka.http.scaladsl.server.Directives._
import com.levi9.dao.Query.QueryPositionDao
import spray.json._
import com.levi9.representation.json._

import scala.concurrent.ExecutionContext.Implicits.global

trait PositionQueryController extends JsonSupport {

  val positionDao: QueryPositionDao

  def position() = (path("position" / "type") & get & pathEnd) (complete(positionDao.findAll().map(_.toJson)))
}
