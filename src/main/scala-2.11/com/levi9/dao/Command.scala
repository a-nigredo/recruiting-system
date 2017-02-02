package com.levi9.dao

import com.levi9.domain.model.Candidates.Candidate
import com.levi9.domain.model.Security.AccessToken
import com.levi9.domain.model.Users.User
import com.levi9.domain.model.Vacancies.Vacancy

import scala.concurrent.Future

/**
  * Module exposes api via type aliases.
  *
  * It prevents direct usage of class from dao package and thus encapsulates module's internals
  */
object Command {

  type CommandCandidateDao = CandidateDao
  type CommandVacancyDao = VacancyDao
  type CommandUserDao = UserDao
  type CommandCommentDao[A] = CommentDao[A]
  type CommandAccessTokenDao = AccessTokenDao
  type CommandPersistableDao[A, B] = PersistableDao[A, Future[B]]

  protected[dao] trait CandidateDao extends PersistableDao[Candidate, Future[Unit]] with ItemByIdDao[Candidate]

  protected[dao] trait VacancyDao extends PersistableDao[Vacancy, Future[Unit]] with ItemByIdDao[Vacancy]

  protected[dao] trait UserDao extends PersistableDao[User, Future[Long]]

  protected[dao] trait CommentDao[A] extends PersistableDao[A, Future[Unit]]

  protected[dao] trait AccessTokenDao extends PersistableDao[AccessToken, Future[Unit]] {

    def prolongExpireDate(token: String, time: Long): Future[Unit]

    def removeToken(token: String): Future[Unit]
  }

}