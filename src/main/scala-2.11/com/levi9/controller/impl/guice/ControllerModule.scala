package com.levi9.controller.impl.guice

import com.google.inject.AbstractModule
import com.levi9.controller.{Controller, FrontController}
import net.codingwell.scalaguice.{ScalaModule, ScalaMultibinder}

class ControllerModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {

    val controller = ScalaMultibinder.newSetBinder[Controller](binder)
    controller.addBinding.to[FormResourceController]
    controller.addBinding.to[CandidateCommandController]
    controller.addBinding.to[CandidateQueryController]
    controller.addBinding.to[VacancyCommandController]
    controller.addBinding.to[VacancyQueryController]
    controller.addBinding.to[UserQueryControllerImpl]
    controller.addBinding.to[UserCommandController]

    bind[Controller].annotatedWithName("LoginController").to[LoginController]
    bind[Controller].annotatedWithName("ResourceController").to[ResourceController]
    bind[FrontController].to[FrontControllerImpl]
  }
}