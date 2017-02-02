package com.levi9.dao.impl.slick.query.filter

import com.levi9.dao.filter.Field
import com.levi9.dao.filter.dsl.Candidates._
import com.levi9.dao.filter.dsl.Position.PositionName
import com.levi9.dao.filter.dsl.Seniority.SeniorityName
import com.levi9.dao.filter.dsl.SourceType.SourceTypeName
import com.levi9.dao.impl.slick.query.SlickCandidateDao
import com.levi9.dao.impl.slick.query.filter.CandidateInterpreter.QueryBuilder

protected[slick] trait CandidateInterpreter extends BasicInterpreter[QueryBuilder] {

  override protected def interpretField(field: Field[_], queryBuilder: QueryBuilder) = field match {
    case CandidateId => queryBuilder._1.id
    case Name => queryBuilder._1.name
    case Surname => queryBuilder._1.surname
    case Email => queryBuilder._1.email
    case Phone => queryBuilder._1.phone
    case Skype => queryBuilder._1.skype
    case SeniorityName => queryBuilder._2.title
    case PositionName => queryBuilder._3.title
    case SourceTypeName => queryBuilder._4.title
  }
}

object CandidateInterpreter {
  type QueryBuilder = SlickCandidateDao#QueryBuilder
}


