package com.levi9.domain.service

import com.levi9.dao.Command.CommandUserDao
import com.levi9.domain.model.Users.User

trait UserService extends Service[User]

object UserService {
  type Dao = CommandUserDao
}