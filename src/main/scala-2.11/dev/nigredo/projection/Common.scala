package com.levi9.projection

import com.levi9.Difference
import com.levi9.domain.model.Storable

object Common {

  case class Author(id: Long, name: String)

  @Difference.ByField("id")
  case class Status(id: Int, title: String) extends Storable[Int]

  case class History(author: String, data: List[Difference], creationDate: Long)

  case class Response(content: List[History], total: Int)
}
