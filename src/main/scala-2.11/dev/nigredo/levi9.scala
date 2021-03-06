package com

import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.sys.SystemProperties
import scalaz.Functor

package object levi9 {

  implicit val system = ActorSystem("recruiting-system")
  implicit val materializer = ActorMaterializer()

  def config = {
    val defaultConfig = ConfigFactory.load()
    new SystemProperties().get("config")
      .map(x => ConfigFactory.parseFile(new File(x)).withFallback(defaultConfig))
      .getOrElse(defaultConfig)
  }

  val officesConfig = config.getConfigList("offices")
  val dataSourceConfig = config.getConfig("dataSource")
  val serverConfig = config.getConfig("server")

  implicit class Product2Future[A](product: A) {
    def toSuccess = Future.successful(product)
  }

  implicit class MapFPimp[F[_] : Functor, G[_] : Functor, A](fa: F[G[A]]) {
    def mapF[B](f: A => B): F[G[B]] = Functor[F].compose[G].map(fa)(f)
  }

}