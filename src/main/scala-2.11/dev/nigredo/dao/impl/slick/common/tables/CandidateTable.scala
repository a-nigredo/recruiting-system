package com.levi9.dao.impl.slick.common.tables

import java.util.Date

import com.levi9.dao.impl.slick.common.Profile
import com.levi9.dao.impl.slick.common.tables.mapper.{DateTimeMapper, IdMapper}
import com.levi9.domain.model.Id
import slick.profile.SqlProfile.ColumnOption.Nullable

protected[slick] trait CandidateTable
  extends Profile
    with VocabularyTable
    with UserTable
    with VacancyTable
    with DateTimeMapper
    with IdMapper {
  parent =>

  import profile.api._

  case class CandidateRow(id: Id, name: String, surname: String, email: String, phone: String, skype: String,
                          seniority: Long, position: Long, source: Long, creationDate: Date, status: Int,
                          author: Long, location: Int, vacancy: Option[String] = None)

  case class CandidateSnapshotRow(id: Id, name: String, surname: String, email: String, phone: String, skype: String,
                                  seniority: Long, position: Long, source: Long, creationDate: Date, status: Int,
                                  author: Long, location: Int, vacancy: Option[String] = None, cv: Option[Id] = None)

  protected abstract class Fields[A](tableName: String, tag: Tag) extends Table[A](tag, tableName) {

    def id = column[Id]("id", O.SqlType("VARCHAR(50)"), O.PrimaryKey)

    def name = column[String]("name")

    def surname = column[String]("surname")

    def email = column[String]("email")

    def phone = column[String]("phone")

    def skype = column[String]("skype")

    def seniority = column[Long]("seniority_id")

    def position = column[Long]("position_id")

    def source = column[Long]("source_type_id")

    def creationDate = column[Date]("creation_date")

    def author = column[Long]("author_id")

    def status = column[Int]("status")

    def location = column[Int]("location")

    def vacancy = column[String]("vacancy_id", Nullable)

    def sourceFk = foreignKey("source_type_candidate_fk", source, sourcesType)(_.id)

    def seniorityFk = foreignKey("seniority_candidate_fk", seniority, seniorities)(_.id)

    def positionFk = foreignKey("position_candidate_fk", position, positions)(_.id)

    def authorFk = foreignKey("author_candidate_fk", author, users)(_.id)

    def vacancyFk = foreignKey("vacancy_id_candidate_fk", vacancy, vacancies)(_.id)

    def candidateIdIndex = index("candidateIdIndex", id, unique = false)
  }

  class Candidates(tag: Tag) extends Fields[CandidateRow]("candidates", tag) {
    def * = (id, name, surname, email, phone, skype, seniority, position, source, creationDate, status, author,
      location, vacancy.?) <> (CandidateRow.tupled, CandidateRow.unapply)
  }

  class CandidatesSnapshot(tag: Tag) extends Fields[CandidateSnapshotRow]("candidates_snapshot", tag) {

    def cv = column[Id]("cv", Nullable)

    //TODO FIx me
    //    def cvFK = foreignKey("cv_candidate_snapshot_fk", cv, cvs)(_.id)

    def * = (id, name, surname, email, phone, skype, seniority, position, source, creationDate, status, author,
      location, vacancy.?, cv.?) <> (CandidateSnapshotRow.tupled, CandidateSnapshotRow.unapply)
  }

  val candidates = TableQuery[Candidates]
  val candidatesSnapshot = TableQuery[CandidatesSnapshot]
}
