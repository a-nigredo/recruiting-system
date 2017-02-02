package com.levi9.controller.api

import akka.http.scaladsl.server.Directives._
import com.levi9.dao.Query.QuerySeniorityDao
import com.levi9.representation.json._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global

trait SeniorityQueryController extends JsonSupport {

  val seniorityDao: QuerySeniorityDao

  def seniority() = (path("seniority" / "type") & get & pathEnd) (complete(seniorityDao.findAll().map(_.toJson)))
}
