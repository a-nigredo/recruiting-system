package com.levi9.controller.api.candidate.command

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.Segment
import com.levi9.controller.UserAwareController
import com.levi9.controller.api.candidate.comment.command.CommentController
import com.levi9.dao.Query.QueryCandidateDao
import com.levi9.dao.filter.dsl.Candidates.CandidateId
import com.levi9.domain.model.Id
import com.levi9.dto.Candidates.{CreateCandidateDto, UpdateCandidateDto}
import com.levi9.projection.User
import com.levi9.representation.json.Query._
import com.levi9.representation.json.command.Candidates._
import com.levi9.service.CandidateService

trait CandidateController
  extends CommentController
    with UserAwareController {

  type CandidateServiceType = CandidateService

  val candidateService: CandidateServiceType
  val queryCandidateDao: QueryCandidateDao

  override def buildRoutes(user: User) = (pathPrefix("candidates") & isCommand) {

    def create(user: User) = (post & entity(as[CreateCandidateDto]) & pathEnd) { candidate =>
      respond(candidateService.create(candidate, user))(x => Created(x.id.toJson.toString))
    }

    def update(id: String, user: User) = (put & entity(as[UpdateCandidateDto]) & pathEnd) { candidate =>
      respond(candidateService.update(id, candidate, user))(_ => Created(Id(id).toJson.toString))
    }

    pathPrefix(Segment) {
      id => onSuccess(queryCandidateDao.findOne(CandidateId == id)(user.location))(_.map(_ => comment(id, user) ~ update(id, user)).getOrElse(NotFound))
    } ~ create(user)
  }
}