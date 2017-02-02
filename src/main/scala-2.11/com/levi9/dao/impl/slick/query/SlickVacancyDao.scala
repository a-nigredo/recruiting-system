package com.levi9.dao.impl.slick.query

import com.levi9.dao.Query.VacancyDao
import com.levi9.dao.filter.Filter
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.impl.slick.common.tables.{CandidateTable, UserTable, VacancyTable, VocabularyTable}
import com.levi9.dao.impl.slick.query.filter.BasicInterpreter
import com.levi9.dao.{Page, Pageable}
import com.levi9.domain.model.Vacancies.Status
import com.levi9.projection.Common.Author
import com.levi9.projection.Vacancies.{Assignee, Vacancy}
import com.levi9.projection.{Location, Position, Seniority}

import scala.concurrent.ExecutionContext.Implicits.global

trait SlickVacancyDao
  extends JDBCDataSourceProvider
    with VacancyDao
    with VacancyTable
    with CandidateTable
    with UserTable {

  import profile.api._

  private type User = UserTable#User

  type QueryBuilder = (VacancyTable#Vacancy, VocabularyTable#Vocabularies, VocabularyTable#Vocabularies, User, User, User)

  val filterInterpreter: BasicInterpreter[QueryBuilder]

  override def findOne(filter: Filter) = (location: Location) =>
    for {
      vacancy <- source.run(query(location).filter(x => filterInterpreter.interpret(filter, x)).result.headOption)
      candidates <- source.run(vacancy.map(x => candidatesSnapshot.filter(_.vacancy === x._1.id).map(_.id).result).getOrElse(DBIO.successful(Nil)))
    } yield vacancy.map(x =>
      toVacancy(x, candidates.map(_.value).toList))

  override def findAll(filter: Option[Filter]) = (page: Pageable, location: Location) => {
    val q = query(location)

    def baseQuery(filter: Filter) = q.filter(x => filterInterpreter.interpret(filter, x))

    (for {
      total <- source.run(vacanciesSnapshot.size.result)
      range = page.rangeFor(total)
      result <- source.run(filter.map(baseQuery).getOrElse(q).drop(range.from).take(page.size).result)
    } yield (result, total)).map { result =>
      val content = result._1.toList.map(vacancy => toVacancy(vacancy, Nil))
      Page(content, result._2)
    }
  }

  private def query(location: Location) = for {
    (((((vacancy, seniority), position), assignee), owner), author) <- vacanciesSnapshot
      .join(seniorities).on(_.seniority === _.id)
      .join(positions).on(_._1.position === _.id)
      .join(users).on(_._1._1.assignee === _.id)
      .join(users).on(_._1._1._1.owner === _.id)
      .join(users).on(_._1._1._1._1.author === _.id)
      .filter {
        case (((((vacancy, _), _), _), _), _) => vacancy.location === location.id
      }.sortBy {
      case (((((vacancy, _), _), _), _), _) => vacancy.creationDate.desc
    }
  } yield (vacancy, seniority, position, assignee, owner, author)

  private def toVacancy(data: (VacancyRow, VocabularyRow, VocabularyRow, UserRow, UserRow, UserRow),
                        candidates: List[String]) = {
    val (vacancy, seniority, position, assignee, owner, author) = data
    val status = Status(vacancy.status)
    Vacancy(vacancy.id, vacancy.project, vacancy.description, vacancy.requirements,
      Seniority(seniority.id, seniority.title), Position(position.id, position.title), Location(vacancy.location),
      vacancy.quantity, Assignee(assignee.id.get, assignee.name), Author(owner.id.get, owner.name),
      vacancy.creationDate.getTime, Author(author.id.get, author.name), candidates,
      com.levi9.projection.Common.Status(status.id, status.title))
  }
}
