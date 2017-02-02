package com.levi9.representation.json.query

import com.levi9.projection.Candidate
import org.json4s.{Formats, NoTypeHints}
import org.json4s.native.Serialization
import org.json4s.native.Serialization._

object Candidates {
  implicit class JsonString2Candidate(value: String) {
    def toCandidate(implicit formats: Formats = Serialization.formats(NoTypeHints)) = read[Candidate](value)
  }
}
