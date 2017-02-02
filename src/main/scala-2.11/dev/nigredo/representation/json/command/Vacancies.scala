package com.levi9.representation.json.command

import com.levi9.dto.Vacancies.{CreateVacancyDto, UpdateVacancyDto}
import spray.json.DefaultJsonProtocol._

object Vacancies {

  implicit val vacancyCreateDtoFormat = jsonFormat7(CreateVacancyDto)
  implicit val vacancyUpdateDtoFormat = jsonFormat8(UpdateVacancyDto)

}