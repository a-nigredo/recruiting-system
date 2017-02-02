package com.levi9.domain.service

import com.levi9._
import com.levi9.common.ValidationError
import com.levi9.dao.Command.CommandPersistableDao

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Try}

trait Service[A] {

  type Dao = CommandPersistableDao[A, _]
  type Validator = com.levi9.domain.validation.Validator[A]
  @throws(classOf[Error])
  type BeforeSave = A => Future[A]

  val validator: Validator
  val dao: Dao

  def transactional[C](onFailure: => Failure[_] => Unit)(body: => C): Try[C] =
    Try(body) match {
      case failure@Failure(f) => onFailure(failure)
        failure
      case success@_ => success
    }

  private def identity(value: A) = value.toSuccess

  @throws(classOf[ValidationError])
  @throws(classOf[Error])
  def save(domain: A)(beforeSave: => BeforeSave = this.identity): Future[A] = for {
    _ <- validate(domain)
    beforeSave <- beforeSave(domain)
    result <- dao.save(beforeSave).map(x => beforeSave)
  } yield result

  private def validate(domain: A) = validator.isValid(domain) map {
    case errors@x :: xs => throw ValidationError(errors)
    case _ => ()
  }
}
