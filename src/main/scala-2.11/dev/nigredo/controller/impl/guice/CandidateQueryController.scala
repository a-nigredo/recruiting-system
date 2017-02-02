package com.levi9.controller.impl.guice

import javax.inject.{Inject, Named}

import com.levi9.controller.SecuredController
import com.levi9.controller.api.candidate.query._
import com.levi9.dao.Query.{QueryCandidateDao, QueryCommentDao, QueryConfigDao}

class CandidateQueryController @Inject()(@Named("SlickQueryCandidateDao") val queryCandidateDao: QueryCandidateDao,
                                         @Named("SlickQueryCommentDao") val queryCommentDao: QueryCommentDao,
                                         val configDao: QueryConfigDao)
  extends CandidateController
    with SecuredController