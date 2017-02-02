package com.levi9.controller.api

import akka.http.scaladsl.server.Directives._
import com.levi9.projection.{Location, Position, Seniority, Source}
import com.levi9.representation.json._

import scala.concurrent.ExecutionContext.Implicits.global

trait VocabularyQueryController
  extends SeniorityQueryController
    with PositionQueryController
    with SourceTypeQueryController
    with LocationQueryController {

  case class Vocabulary(seniority: List[Seniority], position: List[Position], source: List[Source], location: List[Location])

  implicit val vocabularyFormat = jsonFormat4(Vocabulary)

  val vocabulary = seniority() ~ position() ~ sourceType() ~ location() ~ (path("vocabulary") & get & pathEnd) {
    val result = for {
      seniority <- seniorityDao.findAll()
      position <- positionDao.findAll()
      sourceType <- sourceTypeDao.findAll()
    } yield Vocabulary(seniority, position, sourceType, Location.all)
    onSuccess(result)(x => Ok(x.toJson.toString))
  }
}
