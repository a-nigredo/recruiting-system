package com.levi9.representation

import com.levi9.controller.api._
import com.levi9.projection.{Location, Position, Seniority}
import spray.json.{DeserializationException, JsNumber, JsObject, JsString, JsValue, JsonFormat}

package object json {

  implicit object DateFormat extends JsonFormat[java.util.Date] {
    def write(obj: java.util.Date) = JsString(obj.getTime.toString)

    def read(json: JsValue): java.util.Date = json match {
      case JsString(value) => new java.util.Date(value.toLong)
      case JsNumber(value) => new java.util.Date(value.toLong)
      case _ => throw DeserializationException("Date is not JsString")
    }
  }

  implicit object LocationFormat extends JsonFormat[Location] {
    def write(obj: Location) = JsObject((obj.id.toString, JsString(obj.title)))

    def read(json: JsValue): Location = json match {
      case JsObject(value) => Location(value.keySet.head.toInt)
      case _ => throw DeserializationException("Location is not JsObject")
    }
  }

  implicit val candidateSeniorityFormat = jsonFormat2(Seniority)
  implicit val candidatePositionFormat = jsonFormat2(Position)
}
