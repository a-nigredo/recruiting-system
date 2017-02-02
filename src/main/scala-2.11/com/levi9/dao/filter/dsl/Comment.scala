package com.levi9.dao.filter.dsl

import com.levi9.dao.filter.Field

object Comment {
  case object IsPrivate extends Field[Boolean]
}
