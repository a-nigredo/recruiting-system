package com.levi9.controller.impl.guice

import javax.inject.Inject

import com.google.inject.name.Named
import com.levi9.controller.SecuredController
import com.levi9.controller.api.vacancy.query.VacancyController
import com.levi9.dao.Query.QueryVacancyDao

class VacancyQueryController @Inject()(@Named("SlickQueryVacancyDao") val queryVacancyDao: QueryVacancyDao)
  extends VacancyController
    with SecuredController