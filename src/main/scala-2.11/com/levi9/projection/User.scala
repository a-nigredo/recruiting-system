package com.levi9.projection

import com.levi9.domain.model.Users.{User => DomainUser}

case class User(id: Long, name: String, email: String, role: Role, location: Location)

case class Role(id: Long, name: String)

object User {
  implicit def View2Domain(user: User): DomainUser = DomainUser(Some(user.id), user.name, user.email, user.role.id, user.location.id)
}
