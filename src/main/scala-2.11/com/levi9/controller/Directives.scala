package com.levi9.controller

import akka.http.scaladsl.server.Directives._
import com.levi9._

object Directives {
  def withPagination = parameters('page.as[Int] ? config.getInt("pagination.startPage"), 'perPage.as[Int] ? config.getInt("pagination.perPage"))
}
