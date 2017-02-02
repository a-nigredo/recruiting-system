package com.levi9.dao.impl.slick.command

import com.levi9.dao.Command.AccessTokenDao
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.impl.slick.common.tables.AccessTokenTable
import com.levi9.domain.model.Security.AccessToken

import scala.concurrent.ExecutionContext.Implicits.global

protected[slick] trait SlickAccessTokenDao
  extends AccessTokenDao
    with AccessTokenTable
    with JDBCDataSourceProvider {

  import profile.api._

  override def save(token: AccessToken) = source.run(tokens += token).map(_ => ())

  override def removeToken(token: String) = source.run(tokens.filter(_.value === token).delete).map(x => ())

  override def prolongExpireDate(token: String, time: Long) = {
    val q = (for {
      t <- tokens if t.value === token
    } yield t.expireDate).update(time)
    source.run(q).map(x => ())
  }

  implicit def Entity2Row(at: AccessToken): AccessTokenRow = AccessTokenRow(at.value, at.expireDate, at.user)
}