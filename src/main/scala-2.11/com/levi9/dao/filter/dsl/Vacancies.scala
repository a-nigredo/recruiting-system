package com.levi9.dao.filter.dsl

import com.levi9.dao.filter.Field

object Vacancies {

  case object VacancyId extends Field[String]

  case object Description extends Field[String]

  case object Requirements extends Field[String]

  case object Project extends Field[String]
}
