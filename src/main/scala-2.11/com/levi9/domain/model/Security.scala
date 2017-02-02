package com.levi9.domain.model

import java.util.Date

object Security {

  case class AccessToken(value: String, expireDate: Long, user: Long) {
    def isExpired(tokenLifeTime: Long) = new Date().getTime - expireDate > tokenLifeTime
  }

}
