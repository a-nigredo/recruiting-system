package com.levi9.service

import com.levi9.common.{SystemError, ValidationError}
import com.levi9.dao.Command.CommandVacancyDao
import com.levi9.domain
import com.levi9.domain.model.Id
import com.levi9.domain.model.Vacancies._
import com.levi9.dto.Updatable
import com.levi9.dto.Vacancies.{CreateVacancyDto, UpdateVacancyDto}
import com.levi9.projection.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import scalaz.OptionT
import scalaz.Scalaz._

trait VacancyService {

  type Dao = CommandVacancyDao
  type DomainService = domain.service.VacancyService

  val domainService: DomainService
  val dao: Dao

  def create(dto: CreateVacancyDto, author: User) = {
    val vacancy = Vacancy(dto.description, dto.requirements, dto.project, dto.seniority, dto.position,
      author.location.id, dto.quantity, dto.assignee, author.id, author.id)
    domainService.save(vacancy)()
  }

  def update(id: String, dto: UpdateVacancyDto with Updatable, author: User) =
    Try {
      if (!dto.isChanged) Future.successful(throw ValidationError("There are no changes" :: Nil))
      else OptionT(dao.findOne(Id(id))).flatMapF { vacancy =>
        val toSave = Vacancy(id = vacancy.id,
          description = dto.description.getOrElse(vacancy.description),
          requirements = dto.requirements.getOrElse(vacancy.requirements),
          project = dto.project.getOrElse(vacancy.project),
          seniority = dto.seniority.getOrElse(vacancy.seniority),
          position = dto.position.getOrElse(vacancy.position),
          location = author.location.id,
          quantity = dto.quantity.getOrElse(vacancy.quantity),
          assignee = dto.assignee.getOrElse(vacancy.assignee),
          owner = vacancy.owner,
          author = author.id,
          creationDate = vacancy.creationDate,
          status = dto.status.map(x => vacancy.status.to(x)).getOrElse(vacancy.status))
        domainService.save(toSave)()
      }.run.map(_.getOrElse(throw SystemError(new Exception(s"Not found vacancy '$id'"))))
    }
}
