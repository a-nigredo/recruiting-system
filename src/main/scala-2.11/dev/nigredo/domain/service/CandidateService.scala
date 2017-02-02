package com.levi9.domain.service

import com.levi9.dao.Command.CommandCandidateDao
import com.levi9.domain.model.Candidates.CandidateDomain

trait CandidateService extends Service[CandidateDomain]

object CandidateService {
  type Dao = CommandCandidateDao
}