package com.levi9.dao.impl.slick.query

import com.levi9.dao.filter.Filter
import com.levi9.dao.Query.PositionDao
import com.levi9.projection.Position

protected[slick] trait SlickPositionDao
  extends PositionDao
    with SlickGenericVocabulary[Position] {

  override def findAll(filter: Option[Filter]) = all(filter, positions)

  override def findOne(filter: Filter) = one(filter, positions)
}

object SlickPositionDao {

  import com.levi9._

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Future
  import scalaz.Scalaz._

  implicit def VocabularyRow2Role(row: VocabularyOptionRow): Future[Option[Position]] = row.mapF(x => Position(x.id, x.title))

  implicit def VocabularyRowList2ListRole(list: VocabularyRowList): Future[List[Position]] = list.mapF(x => Position(x.id, x.title))
}