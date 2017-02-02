package com.levi9.dao.impl.slick.common.tables

import java.util.Date

import com.levi9.dao.impl.slick.common.Profile
import com.levi9.dao.impl.slick.common.tables.mapper.DateTimeMapper

protected[slick] trait VacancyTable
  extends Profile
    with UserTable
    with DateTimeMapper {

  import profile.api._

  case class VacancyInterviewerRow(vacancy: String, interviewer: Long)

  case class VacancyRow(id: String, project: String, description: String, requirements: String, seniority: Long,
                        position: Long, location: Int, quantity: Int, assignee: Long, owner: Long,
                        creationDate: Date, author: Long, status: Int)

  class Vacancy(name: String, tag: Tag) extends Table[VacancyRow](tag, name) {

    def id = column[String]("id", O.SqlType("VARCHAR(50)"), O.PrimaryKey)

    def description = column[String]("description")

    def requirements = column[String]("requirements")

    def project = column[String]("project")

    def seniority = column[Long]("seniority_id")

    def position = column[Long]("position_id")

    def location = column[Int]("location_id")

    def quantity = column[Int]("quantity")

    def assignee = column[Long]("assignee_id")

    def owner = column[Long]("owner_id")

    def creationDate = column[Date]("creation_date")

    def author = column[Long]("author_id")

    def status = column[Int]("status_id")

    def assigneeFk = foreignKey("assignee_vacancy_fk", seniority, users)(_.id)

    def seniorityFk = foreignKey("seniority_vacancy_fk", seniority, seniorities)(_.id)

    def positionFk = foreignKey("position_vacancy_fk", position, positions)(_.id)

    def authorFk = foreignKey("author_vacancy_fk", author, users)(_.id)

    def ownerFk = foreignKey("owner_vacancy_fk", owner, users)(_.id)

    def idIndex = index("vacancy_id", id, unique = false)

    def * = (id, project, description, requirements, seniority, position, location, quantity,
      assignee, owner, creationDate, author, status) <> (VacancyRow.tupled, VacancyRow.unapply)
  }

  lazy val vacancies = TableQuery(new Vacancy("vacancies", _))
  lazy val vacanciesSnapshot = TableQuery(new Vacancy("vacancies_snapshot", _))
}