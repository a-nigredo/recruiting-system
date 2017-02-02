package com.levi9.controller.impl.guice

import javax.inject.Inject

import com.google.inject.name.Named
import com.levi9.controller.SecuredController
import com.levi9.controller.api.user.command.UserController
import com.levi9.dao.Query.PageableQueryUserDao

class UserCommandController @Inject()(val userService: UserController#UserServiceType,
                                      @Named("SlickPageableQueryUserDao") val queryDao: PageableQueryUserDao)
  extends UserController
    with SecuredController