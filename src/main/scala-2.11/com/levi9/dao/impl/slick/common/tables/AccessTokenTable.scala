package com.levi9.dao.impl.slick.common.tables

import com.levi9.dao.impl.slick.common.Profile

protected[slick] trait AccessTokenTable
  extends Profile
    with UserTable {

  import profile.api._

  case class AccessTokenRow(value: String, expireDate: Long, user: Long)

  class AccessTokens(tag: Tag) extends Table[AccessTokenRow](tag, "access_token") {

    def value = column[String]("value")

    def expireDate = column[Long]("expire_date")

    def user = column[Long]("user_id")

    def userFk = foreignKey("user_access_token_fk", user, users)(_.id)

    def * = (value, expireDate, user) <> (AccessTokenRow.tupled, AccessTokenRow.unapply)
  }

  val tokens = TableQuery[AccessTokens]

}

