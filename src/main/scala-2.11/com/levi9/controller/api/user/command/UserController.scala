package com.levi9.controller.api.user.command

import akka.http.scaladsl.server.Directives._
import com.levi9._
import com.levi9.controller.SimpleController
import com.levi9.controller.api.JsonSupport
import com.levi9.dao.Query.PageableQueryUserDao
import com.levi9.dao.filter.dsl.User.UserId
import com.levi9.domain.service.UserService
import com.levi9.dto.Users.UserDto
import com.levi9.representation.json.command.Users._

trait UserController
  extends JsonSupport
    with SimpleController {

  type UserServiceType = UserService

  val userService: UserServiceType
  val queryDao: PageableQueryUserDao

  override protected def buildRoutes() =
    (pathPrefix("users" / LongNumber) & put & entity(as[UserDto]) & pathEndOrSingleSlash) { (id, userDto) =>
      onSuccess(queryDao.findOne(UserId == id)) {
        case Some(user) => respond(userService.save(user)(_.copy(role = userDto.role).toSuccess))(x => Ok())
        case None => NotFound
      }
    }
}
