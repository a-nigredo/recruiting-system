package com.levi9.dao.filter.dsl

import com.levi9.dao.filter.Field

object Status {

  case object StatusId extends Field[Long]

  case object StatusName extends Field[String]

}
