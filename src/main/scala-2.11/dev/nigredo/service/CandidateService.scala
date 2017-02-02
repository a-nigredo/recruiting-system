package com.levi9.service

import com.levi9._
import com.levi9.common.{SystemError, ValidationError}
import com.levi9.dao.Command.CommandCandidateDao
import com.levi9.dao.Query.QueryConfigDao
import com.levi9.domain.model.Candidates.Status.{OnHold, Screening}
import com.levi9.domain.model.Candidates.{Candidate, NewCv, Status}
import com.levi9.domain.model.Id
import com.levi9.dto.Candidates.{CreateCandidateDto, UpdateCandidateDto}
import com.levi9.dto.Updatable
import com.levi9.projection.User

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import scalaz.OptionT
import scalaz.Scalaz._

trait CandidateService {

  type Dao = CommandCandidateDao
  type DomainService = domain.service.CandidateService

  val domainService: DomainService
  val dao: Dao
  val configDao: QueryConfigDao

  def create(dto: CreateCandidateDto, author: User) = {
    val candidate = Candidate(dto.name, dto.surname, dto.email, dto.phone, dto.skype, dto.seniority, dto.position,
      dto.source, author.id, author.location.id)
    domainService.save(dto.cv.map(x => candidate.copy(cv = Some(NewCv(fileName = x.name, content = x.content, candidate = candidate.id)))).getOrElse(candidate))()
  }

  def update(id: String, dto: UpdateCandidateDto with Updatable, author: User) = Try {
    if (!dto.isChanged) Future.successful(throw ValidationError("There are no changes" :: Nil))
    else OptionT(dao.findOne(Id(id))).flatMapF { candidate =>
      val status = if (candidate.vacancy.isEmpty && dto.vacancy.isDefined) Screening.id
      else dto.status.getOrElse(candidate.status.id)
      val vacancy = dto.vacancy match {
        case value@Some(vid) if !candidate.vacancy.contains(Id(vid)) || candidate.vacancy.isEmpty =>
          if (Status(candidate.status) == OnHold || Status(candidate.status) == Screening) value.map(Id(_))
          else throw ValidationError(s"It is not allowed to assign vacancy" :: Nil)
        case _ => candidate.vacancy
      }
      val cId = Id(id)
      val toSave = Candidate(id = cId,
        name = dto.name.getOrElse(candidate.name),
        surname = dto.surname.getOrElse(candidate.surname),
        email = dto.email.getOrElse(candidate.email),
        phone = dto.phone.getOrElse(candidate.phone),
        skype = dto.skype.getOrElse(candidate.skype),
        seniority = dto.seniority.getOrElse(candidate.seniority),
        position = dto.position.getOrElse(candidate.position),
        source = dto.source.getOrElse(candidate.source),
        author = author.id,
        status = candidate.status.to(status),
        vacancy = vacancy,
        creationDate = candidate.creationDate,
        location = candidate.location,
        cv = candidate.cv)
      domainService.save(dto.cv.map(x => toSave.copy(cv = Some(NewCv(fileName = x.name, content = x.content, candidate = cId)))).getOrElse(toSave))()
    }.run.map(_.getOrElse(throw SystemError(new Exception(s"Not found candidate '$id'"))))
  }
}