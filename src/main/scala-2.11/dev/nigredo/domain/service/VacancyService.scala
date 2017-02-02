package com.levi9.domain.service

import com.levi9.dao.Command.CommandVacancyDao
import com.levi9.domain.model.Vacancies.VacancyDomain

trait VacancyService extends Service[VacancyDomain]

object VacancyService {
  type Dao = CommandVacancyDao
}
