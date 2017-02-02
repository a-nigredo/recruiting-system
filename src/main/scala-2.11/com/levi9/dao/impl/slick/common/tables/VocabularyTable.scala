package com.levi9.dao.impl.slick.common.tables

import com.levi9.dao.impl.slick.common.Profile

protected[slick] trait VocabularyTable extends Profile {

  import profile.api._

  case class VocabularyRow(id: Long, title: String)

  class Vocabularies(tableName: String, tag: Tag) extends Table[VocabularyRow](tag, tableName) {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def title = column[String]("title")

    def * = (id, title) <> (VocabularyRow.tupled, VocabularyRow.unapply)
  }

  lazy val seniorities = TableQuery(new Vocabularies("seniority", _))
  lazy val positions = TableQuery(new Vocabularies("position", _))
  lazy val sourcesType = TableQuery(new Vocabularies("source_type", _))
  lazy val roles = TableQuery(new Vocabularies("roles", _))
  lazy val candidateStatus = TableQuery(new Vocabularies("candidate_status", _))
}

