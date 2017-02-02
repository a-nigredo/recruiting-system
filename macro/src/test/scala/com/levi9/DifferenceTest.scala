package com.levi9

import org.scalatest.{FunSuite, Matchers}

class DifferenceTest
  extends FunSuite
    with Matchers {

  case class Primitive(value: Int)

  case class OptionalPrimitive(value: Option[Int])

  test("Primitive is different") {
    val v1 = Primitive(1)
    val v2 = Primitive(2)

    Difference(v1, v2) shouldBe List(Difference("Primitive", "value", "1", "2", None))
  }

  test("Optional primitive is different") {
    val v1 = OptionalPrimitive(Some(1))
    val v2 = OptionalPrimitive(Some(2))

    Difference(v1, v2) shouldBe List(Difference("OptionalPrimitive", "value", "1", "2", None))
  }
}
