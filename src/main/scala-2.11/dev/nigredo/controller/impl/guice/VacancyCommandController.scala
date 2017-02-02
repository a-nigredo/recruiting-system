package com.levi9.controller.impl.guice

import javax.inject.Inject

import com.google.inject.name.Named
import com.levi9.controller.api.vacancy.command.VacancyController

class VacancyCommandController @Inject()(val vacancyService: VacancyController#VacancyServiceType,
                                         @Named("SlickQueryVacancyDao") val vacancyQueryDao: VacancyController#VacancyQueryDao)
  extends VacancyController