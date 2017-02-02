package com.levi9.domain.validation.accord

import com.levi9.dao.Query._
import com.levi9.dao.filter.dsl.Position.PositionId
import com.levi9.dao.filter.dsl.Seniority.SeniorityId
import com.levi9.dao.filter.dsl.SourceType.SourceTypeId
import com.levi9.dao.filter.dsl.Vacancies.VacancyId
import com.levi9.domain.model.Candidates.{CandidateDomain, Cv}
import com.levi9.domain.validation.Validator
import com.levi9.projection.Vacancies.Vacancy
import com.levi9.projection.{Location, Position, Seniority, Source}
import com.wix.accord._
import com.wix.accord.dsl._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait CandidateValidator
  extends Validator[CandidateDomain] {

  val seniorityDao: QuerySeniorityDao
  val positionDao: QueryPositionDao
  val sourceTypeDao: QuerySourceTypeDao
  val vacancyDao: QueryVacancyDao
  val configDao: QueryConfigDao

  implicit val persistentValidators = validator[CandidateDomain] { candidate =>
    candidate.name as "name has to be more then 2 symbols and consist of letters only" is matchRegexFully( """^([\w]{2,})+$""")
    candidate.surname as "surname has to be more then 2 symbols and consist of letters only" is matchRegexFully( """^([\w]{2,})+$""")
    candidate.phone as "phone has to be more then 2 symbols and consist of digit only" is matchRegexFully( """^([\d]{2,})+$""")
    candidate.email as "wrong email format" is matchRegexFully( """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""")
  }

  implicit val seniorityValidation = validator[Option[Seniority]] { seniority =>
    seniority as "non existing seniority" is notEmpty
  }

  implicit val positionValidation = validator[Option[Position]] { position =>
    position as "non existing position" is notEmpty
  }

  implicit val sourceTypeValidation = validator[Option[Source]] { sourceType =>
    sourceType as "non existing source type" is notEmpty
  }

  implicit val cvValidation = validator[(Cv, Location)] { value =>
    configDao(value._2).cv.allowedFiles.contains(value._1.extension) as s"invalid cv format. Allow: ${configDao(value._2).cv.allowedFiles.mkString(",")}" is true
  }

  implicit val vacancyValidation = validator[(CandidateDomain, Option[Vacancy])] { vacancy =>
    vacancy._2 as "non existing vacancy" is notEmpty
  }

  override def isValid(candidate: CandidateDomain) = for {
    seniority <- seniorityDao.findOne(SeniorityId == candidate.seniority)
    position <- positionDao.findOne(PositionId == candidate.position)
    sourceType <- sourceTypeDao.findOne(SourceTypeId == candidate.source)
    vacancy <- candidate.vacancy.map(x => vacancyDao.findOne(VacancyId == x.value)(Location(candidate.location))
      .map(x => validate((candidate, x)))).getOrElse(Future.successful(Success))
  } yield validate(candidate)
    .and(validate(seniority))
    .and(validate(position))
    .and(validate(sourceType))
    .and(candidate.cv.map(x => validate((x, Location(candidate.location)))).getOrElse(Success))
    .and(vacancy)
}