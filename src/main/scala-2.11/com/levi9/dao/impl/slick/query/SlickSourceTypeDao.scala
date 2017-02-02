package com.levi9.dao.impl.slick.query

import com.levi9.dao.filter.Filter
import com.levi9.dao.Query.SourceTypeDao
import com.levi9.projection.Source

protected[slick] trait SlickSourceTypeDao
  extends SourceTypeDao
    with SlickGenericVocabulary[Source] {

  override def findAll(filter: Option[Filter]) = all(filter, sourcesType)

  override def findOne(filter: Filter) = one(filter, sourcesType)
}

object SlickSourceTypeDao {

  import com.levi9._

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Future
  import scalaz.Scalaz._

  implicit def VocabularyRow2Role(row: VocabularyOptionRow): Future[Option[Source]] = row.mapF(x => Source(x.id, x.title))

  implicit def VocabularyRowList2ListRole(list: VocabularyRowList): Future[List[Source]] = list.mapF(x => Source(x.id, x.title))
}