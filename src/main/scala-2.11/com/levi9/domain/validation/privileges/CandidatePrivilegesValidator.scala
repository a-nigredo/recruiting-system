package com.levi9.domain.validation.privileges

import com.levi9.dao.Command.CommandCandidateDao
import com.levi9.dao.Query.QueryUserDao
import com.levi9.dao.filter.dsl.User.UserId
import com.levi9.domain.model.Candidates.Candidate
import com.levi9.domain.validation.Validator
import com.levi9.security.PrivilegesManager

import scala.concurrent.ExecutionContext.Implicits.global

trait CandidatePrivilegesValidator extends Validator[Candidate] {

  type CandidateQueryDao = CommandCandidateDao
  type UserQueryDao = QueryUserDao

  val candidateQueryDao: CandidateQueryDao
  val userQueryDao: UserQueryDao

  override def isValid(entity: Candidate) = for {
    lastState <- candidateQueryDao.findOne(entity.id)
    user <- userQueryDao.findOne(UserId == entity.author)
  } yield lastState match {
    case Some(value) => implicitly[PrivilegesManager[Candidate]].canChange(user.map(_.role.name).get, entity, value)
      .map(x => s"It is not allowed to change field - ${x.path.toLowerCase}")
    case None => Nil
  }
}
