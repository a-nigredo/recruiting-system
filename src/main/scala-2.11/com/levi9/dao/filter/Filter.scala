package com.levi9.dao.filter

import scala.annotation.tailrec

trait Filter {

  import Filter._

  def and(expr: Filter) = And(this, expr)

  def or(expr: Filter) = Or(this, expr)
}

object Filter {

  case class And(leftExpr: Filter, rightExpr: Filter) extends Filter

  case class Or(leftExpr: Filter, rightExpr: Filter) extends Filter

  @tailrec
  def or(filter: Filter, tail: List[Operation[Long]]): Filter =
    if (tail.isEmpty) filter
    else or(filter.or(tail.head), tail.tail)

  @tailrec
  def and(filter: Filter, tail: List[Operation[Long]]): Filter =
    if (tail.isEmpty) filter
    else and(filter.and(tail.head), tail.tail)
}