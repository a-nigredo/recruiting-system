package com.levi9.dao.impl.slick.common.tables

import java.util.Date

import com.levi9.dao.impl.slick.common.Profile
import com.levi9.dao.impl.slick.common.tables.mapper.{DateTimeMapper, IdMapper}
import com.levi9.domain.model.Id

protected[slick] trait CommentTable
  extends Profile
    with UserTable
    with CandidateTable
    with DateTimeMapper
    with IdMapper {

  import profile.api._

  case class CommentRow(id: Id, body: String, isPrivate: Boolean, candidate: Id, author: Long, creationDate: Date)

  class Comment(tag: Tag) extends Table[CommentRow](tag, "comments") {

    def id = column[Id]("id", O.SqlType("VARCHAR(50)"))

    def body = column[String]("body")

    def isPrivate = column[Boolean]("is_private")

    def author = column[Long]("author_id")

    def candidate = column[Id]("candidate_id", O.SqlType("VARCHAR(50)"))

    def creationDate = column[Date]("creation_date")

    def authorFk = foreignKey("author_comment_fk", author, users)(_.id)

    def candidateFk = foreignKey("candidate_comment_fk", candidate, candidates)(_.id)

    def idIndex = index("commentIdIndex", id, unique = false)

    def * = (id, body, isPrivate, candidate, author, creationDate) <> (CommentRow.tupled, CommentRow.unapply)
  }

  val comments = TableQuery[Comment]

}

