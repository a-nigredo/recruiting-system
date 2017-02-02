package com.levi9.controller.api

import akka.http.scaladsl.server.Directives._
import com.levi9.projection.Location
import spray.json._
import com.levi9.representation.json._

trait LocationQueryController extends JsonSupport {

  def location() = (path("location" / "type") & get & pathEnd) (complete(Location.all.map(_.toJson)))
}
