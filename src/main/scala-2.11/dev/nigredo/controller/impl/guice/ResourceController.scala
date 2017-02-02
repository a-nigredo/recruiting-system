package com.levi9.controller.impl.guice

import akka.http.scaladsl.server.Directives._
import com.levi9._
import com.levi9.controller.SimpleController

class ResourceController extends SimpleController {

  private val frontendConfig = config.getConfig("server.frontend")
  private val assetsPath = frontendConfig.getString("assetsPath")
  private val assetsUrl = frontendConfig.getString("assetsUrl")

  override def buildRoutes() = pathPrefix(assetsUrl) {
    path(Segments) { folders =>
      pathEndOrSingleSlash(getFromDirectory(assetsPath + "/" + folders.fold("")(_ + "/" + _)))
    }
  } ~ pathPrefix("")(getFromFile(frontendConfig.getString("indexPage")))
}