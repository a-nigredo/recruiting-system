package com.levi9.dao.impl.slick.query

import com.levi9._
import com.levi9.dao.filter.Filter
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.impl.slick.common.tables.{CandidateTable, CvTable, UserTable, VocabularyTable}
import com.levi9.dao.impl.slick.query.filter.CandidateInterpreter
import com.levi9.dao.{Page, Pageable, Query}
import com.levi9.domain.model.Candidates.Status
import com.levi9.projection.Common.Author
import com.levi9.projection.{Location, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.Scalaz._

protected[slick] trait SlickCandidateDao
  extends JDBCDataSourceProvider
    with CandidateTable
    with CvTable
    with UserTable
    with Query.Candidate.CandidateDao {

  import profile.api._

  type QueryBuilder = (CandidatesSnapshot, Vocabularies, Vocabularies, Vocabularies, User, Rep[Option[CV]])

  val filterInterpreter: CandidateInterpreter

  override def findOne(filter: Filter) = (location: Location) =>
    source.run(query(location).filter(x => filterInterpreter.interpret(filter, x)).result.headOption).mapF(toCandidate)

  override def findAll(filter: Option[Filter]) = (page: Pageable, location: Location) => {
    val q = query(location)

    def baseQuery(filter: Filter) = q.filter(x => filterInterpreter.interpret(filter, x))

    (for {
      total <- source.run(candidatesSnapshot.size.result)
      range = page.rangeFor(total)
      result <- source.run(filter.map(baseQuery).getOrElse(q).drop(range.from).take(page.size).result)
    } yield (result, total)).map(x => Page(x._1.seq.toList.map(toCandidate), x._2))
  }

  private def query(location: Location) = for {
    (((((candidate, seniority), position), sourceType), user), cv) <- candidatesSnapshot
      .join(seniorities).on(_.seniority === _.id)
      .join(positions).on(_._1.position === _.id)
      .join(sourcesType).on(_._1._1.source === _.id)
      .join(users).on(_._1._1._1.author === _.id)
      .joinLeft(cvs).on(_._1._1._1._1.cv === _.id)
      .filter {
        case (((((candidate, _), _), _), _), _) => candidate.location === location.id
      }.sortBy {
      case (((((candidate, _), _), _), _), _) => candidate.creationDate.desc
    }
  } yield (candidate, seniority, position, sourceType, user, cv)

  private def toCandidate(data: (CandidateSnapshotRow, VocabularyRow, VocabularyRow, VocabularyRow, UserRow, Option[CvRow])) = {
    val (candidate, seniority, position, sourceType, user, cv) = data
    val status = Status(candidate.status)
    Candidate(candidate.id, candidate.name, candidate.surname, candidate.email, candidate.phone, candidate.skype,
      Source(sourceType.id, sourceType.title), Seniority(seniority.id, seniority.title), Position(position.id, position.title),
      candidate.creationDate.getTime, com.levi9.projection.Common.Status(status.id, status.title),
      Author(user.id.get, user.name), cv.map(x => Cv(x.name, content = x.content)), cv.isDefined, candidate.vacancy)
  }
}
