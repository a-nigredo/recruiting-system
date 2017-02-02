package com.levi9.dao.impl.slick.common

import com.levi9.dao.impl.slick.common.tables._

protected[slick] trait Tables
  extends CvTable
    with VocabularyTable
    with CandidateTable
    with UserTable
    with CommentTable
    with AccessTokenTable
    with VacancyTable {

  import profile.api._

  lazy val schemas = seniorities.schema ++ positions.schema ++ sourcesType.schema ++ users.schema ++ cvs.schema ++ candidates.schema ++ tokens.schema ++ comments.schema ++ vacancies.schema ++ roles.schema ++ candidateStatus.schema
}