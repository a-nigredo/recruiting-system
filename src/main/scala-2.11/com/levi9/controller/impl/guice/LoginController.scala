package com.levi9.controller.impl.guice

import javax.inject.Inject

import com.levi9.controller.api
import com.levi9.dao.Query._

class LoginController @Inject()(val authService: api.LoginController#AuthServiceType,
                                val configDao: QueryConfigDao)
  extends api.LoginController