package com.levi9.dao.impl.slick.query

import com.levi9.dao.filter.Filter
import com.levi9.dao.Query.SeniorityDao
import com.levi9.projection.Seniority

protected[slick] trait SlickSeniorityDao
  extends SeniorityDao
    with SlickGenericVocabulary[Seniority] {

  override def findOne(filter: Filter) = one(filter, seniorities)

  override def findAll(filter: Option[Filter]) = all(filter, seniorities)
}

object SlickSeniorityDao {

  import com.levi9._

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Future
  import scalaz.Scalaz._

  implicit def VocabularyRow2Role(row: VocabularyOptionRow): Future[Option[Seniority]] = row.mapF(x => Seniority(x.id, x.title))

  implicit def VocabularyRowList2ListRole(list: VocabularyRowList): Future[List[Seniority]] = list.mapF(x => Seniority(x.id, x.title))
}