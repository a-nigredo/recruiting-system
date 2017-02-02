package com.levi9.domain.validation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ValidatorFacade[A] extends Validator[A] {

  val validators: Set[Validator[A]]

  override def isValid(entity: A) = Future.sequence(validators.map(_.isValid(entity))).map(_.toList.flatten)
}
