package com.levi9.dao.impl.slick.query

import com.levi9.dao.filter.Filter
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.impl.slick.common.tables.{CommentTable, UserTable}
import com.levi9.dao.impl.slick.query.filter.BasicInterpreter
import com.levi9.dao.{Page, Pageable, Query}
import com.levi9.projection.Comment
import com.levi9.projection.Common.Author

import scala.concurrent.ExecutionContext.Implicits.global

trait SlickCandidateCommentDao
  extends JDBCDataSourceProvider
    with CommentTable
    with Query.Candidate.CommentDao {

  import profile.api._

  type QueryBuilder = (CommentTable#Comment, UserTable#User)

  val filterInterpreter: BasicInterpreter[QueryBuilder]

  override def findAll(filter: Option[Filter]) = (page: Pageable) => {
    val query = for {
      comment <- comments
      author <- users if comment.author === author.id
    } yield (comment, author)

    def baseQuery(filter: Filter) = query.filter(x => filterInterpreter.interpret(filter, x))

    (for {
      total <- source.run(comments.size.result)
      range = page.rangeFor(total)
      result <- source.run(filter.map(baseQuery).getOrElse(query).drop(range.from).take(page.size).sortBy(_._1.creationDate.desc).result)
    } yield result).map(x => Page(x.seq.toList.map { result =>
      val (comment, author) = result
      Comment(comment.id, comment.body, comment.isPrivate, comment.creationDate.getTime, Author(author.id.get, author.name),
        comment.candidate)
    }, x.size))
  }
}
