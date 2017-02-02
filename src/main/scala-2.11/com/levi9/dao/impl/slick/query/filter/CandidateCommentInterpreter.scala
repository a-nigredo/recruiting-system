package com.levi9.dao.impl.slick.query.filter

import com.levi9.dao.filter.Field
import com.levi9.dao.filter.dsl.Candidates.CandidateId
import com.levi9.dao.filter.dsl.Comment.IsPrivate
import com.levi9.dao.impl.slick.query.SlickCandidateCommentDao
import com.levi9.dao.impl.slick.query.filter.CandidateCommentInterpreter.QueryBuilder

protected[slick] trait CandidateCommentInterpreter extends BasicInterpreter[QueryBuilder] {

  override protected def interpretField(field: Field[_], queryBuilder: QueryBuilder) = field match {
    case CandidateId => queryBuilder._1.candidate
    case IsPrivate => queryBuilder._1.isPrivate
  }
}

object CandidateCommentInterpreter {
  type QueryBuilder = SlickCandidateCommentDao#QueryBuilder
}


