package com.levi9.dao.filter

trait Field[A] extends Filter {

  def ==(value: A): Operation[A] = Equals[A](this, value)

  def >(value: A): Operation[A] = GreaterThan[A](this, value)

  def >=(value: A): Operation[A] = GreaterThanOrEqual[A](this, value)

  def <(value: A): Operation[A] = LessThan[A](this, value)

  def <=(value: A): Operation[A] = LessThanOrEqual[A](this, value)

  def consist(value: A): Operation[A] = Consist[A](this, value)
}

sealed trait Operation[A] extends Filter {

  val field: Field[A]
  val value: A
}

case class Equals[A](field: Field[A], value: A) extends Operation[A]

case class Consist[A](field: Field[A], value: A) extends Operation[A]

case class GreaterThan[A](field: Field[A], value: A) extends Operation[A]

case class GreaterThanOrEqual[A](field: Field[A], value: A) extends Operation[A]

case class LessThan[A](field: Field[A], value: A) extends Operation[A]

case class LessThanOrEqual[A](field: Field[A], value: A) extends Operation[A]