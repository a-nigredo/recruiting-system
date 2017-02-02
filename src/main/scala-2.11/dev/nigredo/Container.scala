package com.levi9

import com.google.inject.Guice
import com.levi9.controller.impl.guice.ControllerModule

object Container {

  val injector = Guice.createInjector(new dao.GuiceModule, new ControllerModule, new domain.validation.impl.GuiceModule,
    new domain.service.impl.GuiceModule, new service.impl.GuiceModule)
}