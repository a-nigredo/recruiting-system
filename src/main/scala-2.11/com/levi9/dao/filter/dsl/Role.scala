package com.levi9.dao.filter.dsl

import com.levi9.dao.filter.Field

object Role {

  case object RoleId extends Field[Long]

  case object RoleName extends Field[String]

}
