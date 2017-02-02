package com.levi9.representation.json.command

import com.levi9.dto.Users.UserDto
import spray.json.DefaultJsonProtocol._

object Users {

  implicit val userDtoFormat = jsonFormat1(UserDto)
}
