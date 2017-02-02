package com.levi9

import akka.http.scaladsl.Http
import com.levi9.controller.FrontController
import com.levi9.dao.Migrator
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global

object RecruitingSystem extends App {

  import com.levi9.Container._
  import net.codingwell.scalaguice.InjectorExtensions._

  val logger = Logger(LoggerFactory.getLogger(this.getClass))
  val host = serverConfig.getString("host")
  val port = serverConfig.getInt("port")

  injector.instance[Migrator].migrate()
  val bindingFuture = Http().bindAndHandle(injector.instance[FrontController].routes()(()), host, port)
  bindingFuture.onSuccess {
    case _ => logger.info(s"Server has been started successfully on host '$host' and port $port")
  }
  bindingFuture.onFailure {
    case ex: Exception => logger.error(s"Failed to bind to $host:$port - $ex")
  }
}
