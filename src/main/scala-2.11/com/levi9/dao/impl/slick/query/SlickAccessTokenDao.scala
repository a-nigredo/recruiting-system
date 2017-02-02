package com.levi9.dao.impl.slick.query

import com.levi9._
import com.levi9.dao.filter.Filter
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.impl.slick.common.tables.AccessTokenTable
import com.levi9.dao.Query.AccessTokenDao
import com.levi9.dao.impl.slick.query.filter.BasicInterpreter
import com.levi9.domain.model.Security.AccessToken

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.Scalaz._

protected[slick] trait SlickAccessTokenDao
  extends AccessTokenDao
    with AccessTokenTable
    with JDBCDataSourceProvider {

  import profile.api._

  val filterInterpreter: BasicInterpreter[AccessTokenTable#AccessTokens]

  override def findOne(filter: Filter) = source.run(tokens
    .filter(x => filterInterpreter.interpret(filter, x))
    .result.headOption).mapF(row => AccessToken(row.value, row.expireDate, row.user))
}