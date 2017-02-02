package com.levi9.domain.validation

import com.levi9.domain.validation.Validator.ValidationError

import scala.concurrent.Future

trait Validator[A] {
  def isValid(entity: A): ValidationError
}

object Validator {
  type ErrorList = List[String]
  type ValidationError = Future[ErrorList]
}