package com.levi9.projection

import com.levi9.Difference
import com.levi9.domain.model.Storable
import com.levi9.projection.Common.{Author, Status}

@Difference.Ignore("author", "creationDate", "isCvExists")
case class Candidate(id: String, name: String, surname: String, email: String, phone: String, skype: String,
                     source: Source, seniority: Seniority, position: Position, creationDate: Long,
                     status: Status, author: Author, cv: Option[Cv], isCvExists: Boolean, vacancy: Option[String])

case class Cv(name: String, content: String)

case class Comment(id: String, body: String, isPrivate: Boolean, creationDate: Long, author: Author, candidate: String)

@Difference.ByField("id")
case class Source(id: Long, title: String)
  extends Storable[Long]
