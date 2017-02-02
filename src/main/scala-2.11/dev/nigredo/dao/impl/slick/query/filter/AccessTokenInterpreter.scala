package com.levi9.dao.impl.slick.query.filter

import com.levi9.dao.filter.Field
import com.levi9.dao.filter.dsl.AccessToken.{AccessTokenUserId, AccessTokenValue}
import com.levi9.dao.impl.slick.common.tables.AccessTokenTable

protected[slick] trait AccessTokenInterpreter extends BasicInterpreter[AccessTokenTable#AccessTokens] {

  override protected def interpretField(field: Field[_], queryBuilder: AccessTokenTable#AccessTokens) =
    field match {
      case AccessTokenValue => queryBuilder.value
      case AccessTokenUserId => queryBuilder.user
    }
}
