package com.levi9.dao.impl.slick.common.tables

import com.levi9.dao.impl.slick.common.Profile

protected[slick] trait UserTable
  extends Profile
    with VocabularyTable {

  import profile.api._

  case class UserRow(id: Option[Long], name: String, email: String, role: Long, location: Int)

  class User(tag: Tag) extends Table[UserRow](tag, "users") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def email = column[String]("email")

    def role = column[Long]("role_id")

    def location = column[Int]("location_id")

    def roleFk = foreignKey("user_role_fk", role, roles)(_.id)

    def * = (id.?, name, email, role, location) <> (UserRow.tupled, UserRow.unapply)
  }

  val users = TableQuery[User]
}