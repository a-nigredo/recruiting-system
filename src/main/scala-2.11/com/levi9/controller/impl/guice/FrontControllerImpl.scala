package com.levi9.controller.impl.guice

import com.google.inject.Inject
import com.google.inject.name.Named
import com.levi9.controller.{Controller, FrontController}
import com.levi9.service.{AuthenticationService, AuthorizationService}

class FrontControllerImpl @Inject()(val authenticationService: AuthenticationService,
                                    val authorizationService: AuthorizationService,
                                    @Named("LoginController") val loginController: Controller,
                                    @Named("ResourceController") val resourceController: Controller,
                                    val controller: Set[Controller]) extends FrontController
