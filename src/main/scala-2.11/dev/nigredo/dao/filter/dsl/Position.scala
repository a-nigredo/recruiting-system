package com.levi9.dao.filter.dsl

import com.levi9.dao.filter.Field

object Position {

  case object PositionId extends Field[Long]

  case object PositionName extends Field[String]

}