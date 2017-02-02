package com.levi9.dao.impl.slick.query.filter

import com.levi9.dao.filter.Field
import com.levi9.dao.filter.dsl.User.{UserEmail, UserId, UserName}
import com.levi9.dao.impl.slick.common.tables.{UserTable, VocabularyTable}
import com.levi9.dao.impl.slick.query.filter.UserInterpreter.QueryBuilder

protected[slick] trait UserInterpreter extends BasicInterpreter[QueryBuilder] {

  override protected def interpretField(field: Field[_], queryBuilder: QueryBuilder) =
    field match {
      case UserEmail => queryBuilder._1.email
      case UserId => queryBuilder._1.id
      case UserName => queryBuilder._1.name
    }
}

object UserInterpreter {
  type QueryBuilder = (UserTable#User, VocabularyTable#Vocabularies)
}
