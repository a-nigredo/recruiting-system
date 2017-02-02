package com.levi9.dao.impl.slick

import com.levi9.dao.impl.slick.common.tables.VocabularyTable

import scala.concurrent.Future

package object query {

  type VocabularyRow = VocabularyTable#VocabularyRow
  type VocabularyOptionRow = Future[Option[VocabularyRow]]
  type VocabularyRowList = Future[List[VocabularyRow]]
}
