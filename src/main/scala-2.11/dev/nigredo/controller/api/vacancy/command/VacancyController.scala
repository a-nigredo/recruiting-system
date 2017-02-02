package com.levi9.controller.api.vacancy.command

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.levi9.controller.UserAwareController
import com.levi9.controller.api.JsonSupport
import com.levi9.dao.Query.QueryVacancyDao
import com.levi9.dao.filter.dsl.Vacancies.VacancyId
import com.levi9.dto.Vacancies.{CreateVacancyDto, UpdateVacancyDto}
import com.levi9.projection.User
import com.levi9.representation.json.Query._
import com.levi9.representation.json.command.Vacancies._
import com.levi9.service.VacancyService

trait VacancyController
  extends JsonSupport
    with UserAwareController {

  type VacancyServiceType = VacancyService
  type VacancyQueryDao = QueryVacancyDao

  val vacancyQueryDao: VacancyQueryDao
  val vacancyService: VacancyServiceType

  override def buildRoutes(user: User) = (pathPrefix("vacancies") & isCommand) {

    def create(user: User): Route = (post & entity(as[CreateVacancyDto])) { dto =>
      respond(vacancyService.create(dto, user))(x => Created(x.id.toJson.toString))
    }

    def update(id: String, user: User): Route = (put & entity(as[UpdateVacancyDto]) & pathEnd) { dto =>
      respond(vacancyService.update(id, dto, user))(x => Created(x.id.toJson.toString))
    }

    pathPrefix(Segment) { id =>
      onSuccess(vacancyQueryDao.findOne(VacancyId == id)(user.location))(result => result.map(_ => update(id, user)).getOrElse(NotFound))
    } ~ create(user)
  }
}