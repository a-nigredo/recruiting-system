package com.levi9.domain.model

object Users {

  case class User(id: Option[Long] = None, name: String, email: String, role: Long, location: Int)
    extends Storable[Option[Long]]

}