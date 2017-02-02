package com.levi9.projection

import com.levi9.Difference
import com.levi9.projection.Common.{Author, Status}

object Vacancies {

  @Difference.ByField("id")
  case class Assignee(id: Long, name: String)

  @Difference.ByField("id")
  case class Interviewer(id: Long, name: String)

  @Difference.Ignore("creationDate", "author")
  case class Vacancy(id: String, project: String, description: String, requirements: String, seniority: Seniority,
                     position: Position, location: Location, quantity: Int, assignee: Assignee, owner: Author,
                     creationDate: Long, author: Author, candidates: List[String] = Nil, status: Status)

}