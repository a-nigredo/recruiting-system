package com.levi9.domain.validation.accord

import com.levi9.dao.Query.QueryRoleDao
import com.levi9.dao.filter.dsl.Role.RoleId
import com.levi9.domain.model.Users.User
import com.levi9.domain.validation.Validator
import com.levi9.projection.Role
import com.wix.accord._
import com.wix.accord.dsl._

import scala.concurrent.ExecutionContext.Implicits.global

trait UserValidator extends Validator[User] {

  val queryRoleDao: QueryRoleDao

  implicit val roleValidation = validator[Option[Role]] { role =>
    role as "non existing role id" is notEmpty
  }

  override def isValid(entity: User) = for {
    role <- queryRoleDao.findOne(RoleId == entity.role)
  } yield validate(role)
}
