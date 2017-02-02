package com.levi9.dao.impl.slick.query.filter

import com.levi9.dao.filter.Field
import com.levi9.dao.filter.dsl.Location.LocationId
import com.levi9.dao.filter.dsl.Position.PositionId
import com.levi9.dao.filter.dsl.Role.RoleId
import com.levi9.dao.filter.dsl.Seniority.SeniorityId
import com.levi9.dao.filter.dsl.SourceType.SourceTypeId
import com.levi9.dao.impl.slick.common.tables.VocabularyTable

protected[slick] trait VocabularyInterpreter extends BasicInterpreter[VocabularyTable#Vocabularies] {

  override protected def interpretField(field: Field[_], queryBuilder: VocabularyTable#Vocabularies) =
    field match {
      case RoleId | SeniorityId | PositionId | SourceTypeId | LocationId => queryBuilder.id
      case _ => queryBuilder.title
    }
}
