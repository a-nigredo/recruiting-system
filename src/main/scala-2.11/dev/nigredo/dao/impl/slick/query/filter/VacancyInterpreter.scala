package com.levi9.dao.impl.slick.query.filter

import com.levi9.dao.filter.Field
import com.levi9.dao.filter.dsl.Position.PositionName
import com.levi9.dao.filter.dsl.Seniority.SeniorityName
import com.levi9.dao.filter.dsl.Vacancies.{Description, Project, Requirements, VacancyId}
import com.levi9.dao.impl.slick.query.SlickVacancyDao
import com.levi9.dao.impl.slick.query.filter.VacancyInterpreter.QueryBuilder

protected[slick] trait VacancyInterpreter extends BasicInterpreter[QueryBuilder] {

  override protected def interpretField(field: Field[_], queryBuilder: QueryBuilder) = field match {
    case VacancyId => queryBuilder._1.id
    case Description => queryBuilder._1.description
    case Requirements => queryBuilder._1.requirements
    case Project => queryBuilder._1.project
    case SeniorityName => queryBuilder._2.title
    case PositionName => queryBuilder._3.title
  }
}

object VacancyInterpreter {
  type QueryBuilder = SlickVacancyDao#QueryBuilder
}


