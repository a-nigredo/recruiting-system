package com.levi9.dao.filter.dsl

import com.levi9.dao.filter.Field

object Candidates {

  case object CandidateId extends Field[String]

  case object Name extends Field[String]

  case object Surname extends Field[String]

  case object Email extends Field[String]

  case object Phone extends Field[String]

  case object Skype extends Field[String]

}
