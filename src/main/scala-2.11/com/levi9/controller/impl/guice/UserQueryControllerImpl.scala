package com.levi9.controller.impl.guice

import javax.inject.Inject

import com.google.inject.name.Named
import com.levi9.controller.SecuredController
import com.levi9.controller.api.user.query.UserController
import com.levi9.dao.Query.{PageableQueryUserDao, QueryRoleDao}

class UserQueryControllerImpl @Inject()(@Named("SlickPageableQueryUserDao") val queryUserDao: PageableQueryUserDao,
                                        @Named("SlickQueryRoleDao") val queryRoleDao: QueryRoleDao)
  extends UserController
    with SecuredController
