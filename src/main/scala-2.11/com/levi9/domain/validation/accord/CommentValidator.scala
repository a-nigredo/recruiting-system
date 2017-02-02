package com.levi9.domain.validation.accord

import com.levi9.domain.model.Candidates.Comment
import com.levi9.domain.validation.Validator
import com.wix.accord._
import com.wix.accord.dsl._

import scala.concurrent.Future

trait CommentValidator extends Validator[Comment] {

  implicit val commentValidator = validator[Comment] { comment =>
    comment.body as "body can't be empty" is notEmpty
  }

  override def isValid(entity: Comment) = Future.successful(validate(entity))
}