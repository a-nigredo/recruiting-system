package com.levi9.service

import com.levi9.dao.Query.QueryConfigDao
import com.levi9.projection.Location

import scala.concurrent.Future

trait AuthorizationService {

  type ConfigDao = QueryConfigDao

  val queryDao: ConfigDao

  def authorize(roleName: String, method: String, url: String, location: Location): Future[Boolean] = {
    Future.successful(queryDao(location).security.acl.filter(_.role.equalsIgnoreCase(roleName)).exists { x =>
      x.resources.filter(_.methods.exists(_.equalsIgnoreCase(method))).map(_.urls.mkString("|")
        .replace("*", "(.*)")).exists(url.matches)
    })
  }
}
