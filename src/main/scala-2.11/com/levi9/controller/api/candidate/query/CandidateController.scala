package com.levi9.controller.api.candidate.query

import akka.http.scaladsl.server.Directives._
import com.levi9.controller.Directives._
import com.levi9.controller.UserAwareController
import com.levi9.controller.api.JsonSupport
import com.levi9.dao.Pageable
import com.levi9.dao.Query.{QueryCandidateDao, QueryConfigDao}
import com.levi9.dao.filter.dsl.Candidates._
import com.levi9.dao.filter.dsl.Position.PositionName
import com.levi9.dao.filter.dsl.Seniority.SeniorityName
import com.levi9.dao.filter.dsl.SourceType.SourceTypeName
import com.levi9.domain.model.Candidates.Status
import com.levi9.projection.Common.{Status => StatusView}
import com.levi9.projection.{Candidate, User}
import com.levi9.representation.json.Query.PageableResponse
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import org.json4s.{FieldSerializer, NoTypeHints}

trait CandidateController
  extends JsonSupport
    with UserAwareController
    with CommentController {

  implicit val formats = Serialization.formats(NoTypeHints) + FieldSerializer[Candidate] {
    FieldSerializer.renameTo("creationDate", "date") orElse FieldSerializer.ignore("cv")
  }

  val queryCandidateDao: QueryCandidateDao
  val configDao: QueryConfigDao

  override def buildRoutes(user: User) = pathPrefix("candidates") {

    def cv(candidate: Option[Candidate]) = (pathPrefix("cv") & pathEnd) {
      candidate match {
        case Some(c) if c.cv.isDefined => Ok(write(c.cv))
        case _ => NotFound
      }
    }

    def toJson(candidate: Candidate) = write(candidate)

    def status(status: StatusView) = (path("status" / "next") & get & pathEnd) (Ok(write(Status.next(status.id).map(x => StatusView(x.id, x.title)))))

    def list = (get & parameters('q.?) & pathEnd) { q =>
      withPagination { (pageNum, perPage) =>
        def anyPredicate(value: String) =
          Name.consist(value) or Surname.consist(value) or Email.consist(value) or Phone.consist(value) or Skype.consist(value) or SeniorityName.consist(value) or PositionName.consist(value) or SourceTypeName.consist(value)

        onSuccess(queryCandidateDao.findAll(q.map(anyPredicate))(Pageable(pageNum, perPage), user.location))(result => {
          Ok(write(PageableResponse(result.items, result.total)))
        })
      }
    }

    list ~ pathPrefix(Segment) { id =>
      onSuccess(queryCandidateDao.findOne(CandidateId == id)(user.location)) { result =>
        result.map(v => status(v.status) ~ cv(result) ~ comment(v.id, user) ~ (get & pathEnd) (Ok(toJson(v)))).getOrElse(NotFound)
      }
    }
  }
}