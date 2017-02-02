package com.levi9.controller.api.vacancy.query

import akka.http.scaladsl.server.Directives._
import com.levi9.controller.Directives._
import com.levi9.controller.UserAwareController
import com.levi9.controller.api.JsonSupport
import com.levi9.dao.Pageable
import com.levi9.dao.Query.QueryVacancyDao
import com.levi9.dao.filter.dsl.Position.PositionName
import com.levi9.dao.filter.dsl.Seniority.SeniorityName
import com.levi9.dao.filter.dsl.Vacancies.{Description, Project, Requirements, VacancyId}
import com.levi9.domain.model.Vacancies.Status
import com.levi9.projection.Common.{Status => StatusView}
import com.levi9.projection.User
import com.levi9.projection.Vacancies.Vacancy
import com.levi9.representation.json.Query.PageableResponse
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import org.json4s.{FieldSerializer, NoTypeHints}

trait VacancyController
  extends JsonSupport
    with UserAwareController {

  implicit val formats = Serialization.formats(NoTypeHints) + FieldSerializer[Vacancy] {
    FieldSerializer.renameTo("creationDate", "date") orElse FieldSerializer.ignore("candidates")
  }

  val queryVacancyDao: QueryVacancyDao

  override def buildRoutes(user: User) = pathPrefix("vacancies") {

    def status(status: StatusView) = (path("status" / "next") & get & pathEnd) (Ok(write(Status.next(status.id).map(x => StatusView(x.id, x.title)))))

    def list = (get & parameters('q.?) & pathEnd) { q =>
      withPagination {
        (pageNum, perPage) =>

          def anyPredicate(value: String) =
            Description.consist(value) or Requirements.consist(value) or Project.consist(value) or SeniorityName.consist(value) or PositionName.consist(value)

          onSuccess(queryVacancyDao.findAll(q.map(anyPredicate))(Pageable(pageNum, perPage), user.location))(result => Ok(write(PageableResponse(result.items, result.total))))
      }
    }

    list ~ pathPrefix(Segment) { id =>
      onSuccess(queryVacancyDao.findOne(VacancyId == id)(user.location)) { vacancy =>
        vacancy.map(v => status(v.status) ~ (get & pathEnd) (Ok(write(v)))).getOrElse(NotFound)
      }
    }
  }
}