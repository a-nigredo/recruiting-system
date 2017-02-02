package com.levi9.dao.impl.slick.common.tables

import java.util.Date

import com.levi9.dao.impl.slick.common.Profile
import com.levi9.dao.impl.slick.common.tables.mapper.{DateTimeMapper, IdMapper}
import com.levi9.domain.model.Id

protected[slick] trait CvTable
  extends Profile
    with CandidateTable
    with DateTimeMapper
    with IdMapper {

  import profile.api._

  case class CvRow(id: Id, name: String, candidate: Id, creationDate: Date, content: String)

  class CV(tag: Tag) extends Table[CvRow](tag, "cv") {

    def id = column[Id]("id", O.SqlType("VARCHAR(50)"), O.PrimaryKey)

    def name = column[String]("name")

    def candidate = column[Id]("candidate_id", O.SqlType("VARCHAR(50)"))

    def creationDate = column[Date]("creation_date")

    def candidateFk = foreignKey("candidate_cv_fk", candidate, candidates)(_.id)

    def content = column[String]("content")

    def cvIdIndex = index("cvIdIndex", id, unique = false)

    def * = (id, name, candidate, creationDate, content) <> (CvRow.tupled, CvRow.unapply)
  }

  val cvs = TableQuery[CV]
}

