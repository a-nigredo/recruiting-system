package com.levi9.dao.impl.slick.command

import com.levi9.dao.Command.CommentDao
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.impl.slick.common.tables.CommentTable
import com.levi9.domain.model.Candidates.{Comment => CommentDomain}

import scala.concurrent.ExecutionContext.Implicits.global

protected[slick] trait SlickCommentDao
  extends CommentDao[CommentDomain]
    with CommentTable
    with JDBCDataSourceProvider {

  import profile.api._

  override def save(comment: CommentDomain) = source.run(for {
    c <- comments += comment
  } yield ())

  implicit def Comment2Row(comment: CommentDomain): CommentRow
  = CommentRow(comment.id, comment.body, comment.isPrivate, comment.candidate, comment.author, comment.creationDate)
}