package com.levi9.domain.service

import com.levi9.dao.Command.CommandCommentDao
import com.levi9.domain.model.Candidates.CandidateComment

trait CommentService extends Service[CandidateComment]

object CommentService {
  type Dao = CommandCommentDao[CandidateComment]
}