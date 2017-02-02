package com.levi9.controller.impl.guice

import javax.inject.{Inject, Named}

import com.levi9.controller.api.candidate.command.CandidateController
import com.levi9.controller.api.candidate.comment.command.CommentController
import com.levi9.dao.Query.QueryCandidateDao

class CandidateCommandController @Inject()(val commentService: CommentController#CommentServiceType,
                                           val candidateService: CandidateController#CandidateServiceType,
                                           @Named("SlickQueryCandidateDao") val queryCandidateDao: QueryCandidateDao)
  extends CandidateController