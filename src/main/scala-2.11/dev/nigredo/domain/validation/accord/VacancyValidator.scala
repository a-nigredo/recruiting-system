package com.levi9.domain.validation.accord

import com.levi9.dao.Query.{QueryPositionDao, QuerySeniorityDao, QueryUserDao}
import com.levi9.dao.filter.dsl.Position.PositionId
import com.levi9.dao.filter.dsl.Seniority.SeniorityId
import com.levi9.dao.filter.dsl.User.UserId
import com.levi9.domain.model.Vacancies.VacancyDomain
import com.levi9.domain.validation.Validator
import com.levi9.projection.{Location, Position, Seniority, User}
import com.wix.accord._
import com.wix.accord.dsl._

import scala.concurrent.ExecutionContext.Implicits.global

trait VacancyValidator extends Validator[VacancyDomain] {

  val seniorityDao: QuerySeniorityDao
  val positionDao: QueryPositionDao
  val userDao: QueryUserDao

  implicit val vacancyValidator = validator[VacancyDomain] { vacancy =>
    vacancy.project as "project can't be empty" is notEmpty
    vacancy.description as "description can't be empty" is notEmpty
    vacancy.requirements as "requirements can't be empty" is notEmpty
    vacancy.quantity as "quantity has to be greater than or equal 0 but less than or equal 5" is within(0 to 5)
    Location.all.exists(_.id == vacancy.location) as "non existing location" is true
  }

  implicit val seniorityValidation = validator[Option[Seniority]] { seniority =>
    seniority as "non existing seniority" is notEmpty
  }

  implicit val positionValidation = validator[Option[Position]] { position =>
    position as "non existing position" is notEmpty
  }

  implicit val assigneeValidation = validator[Option[User]] { user =>
    user as "non existing assignee" is notEmpty
  }

  override def isValid(vacancy: VacancyDomain) = {
    for {
      seniority <- seniorityDao.findOne(SeniorityId == vacancy.seniority)
      position <- positionDao.findOne(PositionId == vacancy.position)
      assignee <- userDao.findOne(UserId == vacancy.assignee)
    } yield validate(vacancy).and(validate(seniority)).and(validate(position)).and(validate(assignee))
  }
}