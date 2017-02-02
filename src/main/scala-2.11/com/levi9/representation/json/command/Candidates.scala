package com.levi9.representation.json.command

import com.levi9.domain.model.Candidates.Status
import com.levi9.domain.model.Candidates.Status._
import com.levi9.dto.Candidates.{CreateCandidateDto, CvDto, UpdateCandidateDto}
import spray.json.DefaultJsonProtocol._
import spray.json.{DeserializationException, JsObject, JsString, JsValue, JsonFormat}

object Candidates {

  implicit object StatusFormat extends JsonFormat[Status] {
    def write(obj: Status) = JsObject((obj.id.toString, JsString(obj.title)))

    def read(json: JsValue): Status = json match {
      case JsObject(value) => Status(value.keySet.head.toInt)
      case _ => throw DeserializationException("Status is not JsObject")
    }
  }

  implicit object CvDtoFormat extends JsonFormat[CvDto] {
    def write(obj: CvDto) = JsString(obj.content.toString)

    def read(json: JsValue): CvDto = json match {
      case JsString(value) => CvDto(value)
      case _ => throw DeserializationException("Cv is not JsString")
    }
  }

  implicit val createCandidateFormat = jsonFormat9(CreateCandidateDto)
  implicit val updateCandidateFormat = jsonFormat11(UpdateCandidateDto)

}
