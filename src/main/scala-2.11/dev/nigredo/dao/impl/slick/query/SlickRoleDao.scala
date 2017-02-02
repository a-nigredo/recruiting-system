package com.levi9.dao.impl.slick.query

import com.levi9.dao.filter.Filter
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.Query.RoleDao
import com.levi9.projection.Role

protected[slick] trait SlickRoleDao
  extends RoleDao
    with JDBCDataSourceProvider
    with SlickGenericVocabulary[Role] {

  override def findOne(filter: Filter) = one(filter, roles)

  override def findAll(filter: Option[Filter]) = all(filter, roles)
}

object SlickRoleDao {

  import com.levi9._

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Future
  import scalaz.Scalaz._

  implicit def VocabularyRowFuture2Role(row: Future[VocabularyRow]): Future[Role] = row.map(x => Role(x.id, x.title))

  implicit def VocabularyRow2Role(row: VocabularyRow): Role = Role(row.id, row.title)

  implicit def VocabularyOptionRow2Role(row: VocabularyOptionRow): Future[Option[Role]] = row.mapF(x => Role(x.id, x.title))

  implicit def VocabularyRowList2ListRole(list: VocabularyRowList): Future[List[Role]] = list.mapF(x => Role(x.id, x.title))
}
