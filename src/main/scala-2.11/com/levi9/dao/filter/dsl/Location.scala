package com.levi9.dao.filter.dsl

import com.levi9.dao.filter.Field


object Location {

  case object LocationId extends Field[Long]

  case object LocationName extends Field[String]

}