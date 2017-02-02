package com.levi9.controller.api.candidate.query

import akka.http.scaladsl.server.Directives._
import com.levi9.controller.Directives._
import com.levi9.controller.api.JsonSupport
import com.levi9.dao.Pageable
import com.levi9.dao.Query.QueryCommentDao
import com.levi9.dao.filter.dsl.Candidates.CandidateId
import com.levi9.projection.User
import com.levi9.representation.json.Query._

trait CommentController extends JsonSupport {

  val queryCommentDao: QueryCommentDao

  def comment(candidateId: String, user: User) = (pathPrefix("comments") & get & pathEnd) {
    withPagination { (pageNum, perPage) =>
      onSuccess(queryCommentDao.findAll(Some(CandidateId == candidateId))(Pageable(pageNum, perPage)))(result => Ok(PageableResponse(result.items, result.total).toJson))
    }
  }
}
