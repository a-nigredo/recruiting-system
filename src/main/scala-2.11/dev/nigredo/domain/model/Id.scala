package com.levi9.domain.model

import java.util.UUID

import org.hashids.Hashids

case class Id(value: String = Hashids(UUID.randomUUID().toString, 8).encode(5L)) extends AnyVal

object Id {
  implicit def Id2String(id: Id): String = id.value

  implicit def OptionId2String(optId: Option[Id]): Option[String] = optId.map(_.value)
}