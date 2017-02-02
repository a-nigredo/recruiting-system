package com.levi9.dao.filter.dsl

import com.levi9.dao.filter.Field

object User {

  case object UserId extends Field[Long]

  case object UserName extends Field[String]

  case object UserEmail extends Field[String]

}
