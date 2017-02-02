package com.levi9.representation.json

import com.levi9.domain.model.Id
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import org.json4s.{Formats, NoTypeHints}

object Query {

  case class PageableResponse[A](content: List[A], total: Int)
  case class Value[A](value: A)

  implicit class AnyRef2Json(value: AnyRef) {
    def toJson(implicit formats: Formats = Serialization.formats(NoTypeHints)) = write(value)
  }

  implicit class Id2Json(value: Id) {
    def toJson(implicit formats: Formats = Serialization.formats(NoTypeHints)) = write(Value(value.value))
  }
}