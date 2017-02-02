package com.levi9.dao.impl.slick.query.filter

import com.levi9.dao.filter._
import com.levi9.dao.impl.slick.common.Profile

protected[query] trait BasicInterpreter[A] extends Profile {

  import profile.api._

  protected def interpretField(field: Field[_], queryBuilder: A): Rep[_]

  protected def resolveCustomType(field: Rep[_], value: Any) = ???

  def interpret(filter: Filter, queryBuilder: A): Rep[Boolean] = {

    def asRep[B](value: Any): Rep[B] = value.asInstanceOf[Rep[B]]

    //TODO investigation about how to avoid such stuff below is needed. Macro?
    def interpretOperation(operation: Operation[_], queryBuilder: A) = {
      val field = interpretField(operation.field, queryBuilder)
      operation match {
        case Consist(_, v) => asRep[String](field) like s"%$v%"
        case op@Equals(_, value) => value match {
          case v: String => asRep[String](field) === v
          case v: Char => asRep[Char](field) === v
          case v: Long => asRep[Long](field) === v
          case v: Int => asRep[Int](field) === v
          case v: Float => asRep[Float](field) === v
          case v: Boolean => asRep[Boolean](field) === v
          case v: Short => asRep[Short](field) === v
          case _ => resolveCustomType(field, value)
        }
        case op@GreaterThan(_, value) => value match {
          case v: String => asRep[String](field) > v
          case v: Char => asRep[Char](field) > v
          case v: Long => asRep[Long](field) > v
          case v: Int => asRep[Int](field) > v
          case v: Float => asRep[Float](field) > v
          case v: Boolean => asRep[Boolean](field) > v
          case v: Short => asRep[Short](field) > v
          case _ => resolveCustomType(field, value)
        }
        case op@GreaterThanOrEqual(_, value) => value match {
          case v: String => asRep[String](field) >= v
          case v: Char => asRep[Char](field) >= v
          case v: Long => asRep[Long](field) >= v
          case v: Int => asRep[Int](field) >= v
          case v: Float => asRep[Float](field) >= v
          case v: Boolean => asRep[Boolean](field) >= v
          case v: Short => asRep[Short](field) >= v
          case _ => resolveCustomType(field, value)
        }
        case op@LessThan(_, value) => value match {
          case v: String => asRep[String](field) < v
          case v: Char => asRep[Char](field) < v
          case v: Long => asRep[Long](field) < v
          case v: Int => asRep[Int](field) < v
          case v: Float => asRep[Float](field) < v
          case v: Boolean => asRep[Boolean](field) < v
          case v: Short => asRep[Short](field) < v
          case _ => resolveCustomType(field, value)
        }
        case op@LessThanOrEqual(_, value) => value match {
          case v: String => asRep[String](field) <= v
          case v: Char => asRep[Char](field) <= v
          case v: Long => asRep[Long](field) <= v
          case v: Int => asRep[Int](field) <= v
          case v: Float => asRep[Float](field) <= v
          case v: Boolean => asRep[Boolean](field) <= v
          case v: Short => asRep[Short](field) <= v
          case _ => resolveCustomType(field, value)
        }
      }
    }

    filter match {
      case operation: Operation[_] => interpretOperation(operation, queryBuilder)
      case field: Field[_] => interpret(field, queryBuilder)
      case Filter.And(f1, f2) => interpret(f1, queryBuilder) && interpret(f2, queryBuilder)
      case Filter.Or(f1, f2) => interpret(f1, queryBuilder) || interpret(f2, queryBuilder)
    }
  }
}