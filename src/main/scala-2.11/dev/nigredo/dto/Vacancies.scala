package com.levi9.dto

object Vacancies {

  case class CreateVacancyDto(project: String, description: String, requirements: String, seniority: Long,
                              position: Long, assignee: Long, quantity: Int)

  case class UpdateVacancyDto(project: Option[String], description: Option[String], requirements: Option[String],
                              seniority: Option[Long], position: Option[Long], assignee: Option[Long],
                              quantity: Option[Int], status: Option[Int])
    extends Updatable

}
