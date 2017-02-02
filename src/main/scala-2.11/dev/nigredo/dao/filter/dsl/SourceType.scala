package com.levi9.dao.filter.dsl

import com.levi9.dao.filter.Field

object SourceType {

  case object SourceTypeId extends Field[Long]

  case object SourceTypeName extends Field[String]

}
