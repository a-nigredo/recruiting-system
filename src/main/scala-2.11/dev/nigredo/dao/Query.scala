package com.levi9.dao

import com.levi9.Config
import com.levi9.domain.model.Security.AccessToken
import com.levi9.projection.Vacancies.Vacancy
import com.levi9.projection._

import scala.concurrent.Future

/**
  * Module exposes api via type aliases.
  *
  * It prevents direct usage of class from dao package and thus encapsulates module's internals
  */
object Query {

  import Candidate._

  type QueryCandidateDao = CandidateDao
  type QueryCandidateHistoryDao = HistoryDao
  type QueryCommentDao = CommentDao
  type QueryVacancyDao = VacancyDao
  type QueryVacancyHistoryDao = HistoryDao
  type QueryAccessTokenDao = AccessTokenDao
  type QueryRoleDao = RoleDao
  type PageableQueryUserDao = PageableUserDao
  type QueryUserDao = UserDao
  type QuerySourceTypeDao = SourceTypeDao
  type QuerySeniorityDao = SeniorityDao
  type QueryPositionDao = PositionDao
  type QueryConfigDao = ConfigDao

  private type FutureOption[A] = Future[Option[A]]
  private type FuturePage[A] = Future[Page[A]]
  private type FutureList[A] = Future[List[A]]

  protected[dao] trait VacancyDao extends ListDao[(Pageable, Location) => FuturePage[Vacancy]]
    with ItemDao[Location => FutureOption[Vacancy]]

  protected[dao] trait AccessTokenDao extends ItemDao[FutureOption[AccessToken]]

  protected[dao] trait RoleDao extends ListDao[FutureList[Role]] with ItemDao[FutureOption[Role]]

  protected[dao] trait PageableUserDao extends ListDao[Pageable => FuturePage[User]]
    with ItemDao[FutureOption[User]]

  protected[dao] trait UserDao extends ListDao[FutureList[User]] with ItemDao[FutureOption[User]]

  protected[dao] trait SourceTypeDao extends ListDao[FutureList[Source]] with ItemDao[FutureOption[Source]]

  protected[dao] trait SeniorityDao extends ListDao[FutureList[Seniority]] with ItemDao[FutureOption[Seniority]]

  protected[dao] trait PositionDao extends ListDao[FutureList[Position]] with ItemDao[FutureOption[Position]]

  protected[dao] object Candidate {

    protected[dao] trait CandidateDao extends ListDao[(Pageable, Location) => FuturePage[Candidate]]
      with ItemDao[Location => FutureOption[Candidate]]

    protected[dao] trait HistoryDao extends ListDao[(Pageable, Location) => FuturePage[History]]

    protected[dao] trait CommentDao extends ListDao[(Pageable) => FuturePage[Comment]]

  }

  protected[dao] trait ConfigDao {
    def apply(location: Location): Config
  }

}
