package com.levi9.dao.impl.slick.command

import com.levi9.dao.Command.UserDao
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import com.levi9.dao.impl.slick.common.tables.UserTable
import com.levi9.domain.model.Users

import scala.concurrent.ExecutionContext.Implicits.global

protected[slick] trait SlickUserDao
  extends UserDao
    with UserTable
    with JDBCDataSourceProvider {

  import profile.api._

  override def save(user: Users.User) = user.id match {
    case Some(_) =>
      val query = (for {
        u <- users if u.id === user.id
      } yield u.role).update(user.role)
      source.run(query).map(_ => user.id.get)
    case None => source.run((users returning users.map(_.id)) += user)
  }

  implicit def Entity2Row(entity: Users.User): UserRow =
    UserRow(entity.id, entity.name, entity.email, entity.role, entity.location)
}