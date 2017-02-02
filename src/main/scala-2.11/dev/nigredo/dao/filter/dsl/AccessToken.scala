package com.levi9.dao.filter.dsl

import com.levi9.dao.filter.Field

object AccessToken {

  case object AccessTokenValue extends Field[String]

  case object AccessTokenUserId extends Field[Long]

}
