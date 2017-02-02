package com.levi9.dao.filter.dsl

import com.levi9.dao.filter.Field

object Seniority {

  case object SeniorityId extends Field[Long]

  case object SeniorityName extends Field[String]

}
