package com.levi9.dto

import java.util.UUID

object Candidates {

  case class CreateCandidateDto(name: String, surname: String, email: String, phone: String, skype: String,
                                seniority: Long, position: Long, source: Long, cv: Option[CvDto])

  case class UpdateCandidateDto(name: Option[String], surname: Option[String], email: Option[String],
                                phone: Option[String], skype: Option[String], seniority: Option[Long],
                                position: Option[Long], source: Option[Long], status: Option[Int], cv: Option[CvDto],
                                vacancy: Option[String]) extends Updatable

  case class CvDto(content: String) {

    private val uuid = UUID.randomUUID().toString

    def name = s"$uuid.${content.substring(5, content.indexOf(";")).split("/").tail.head}"
  }

  case class CandidateCommentDto(body: String, isPrivate: Boolean, candidate: String, author: Long)

}
