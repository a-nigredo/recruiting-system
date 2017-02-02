package com.levi9.projection

import com.levi9.common.SystemError

sealed trait Location {
  val id: Int
  val title: String
}

object Location {

  private case class LocationImpl(id: Int, title: String) extends Location

  private val location =
    Map(1 -> "Kiev", 2 -> "Amsterdam", 3 -> "Belgrad", 4 -> "Novi Sad", 5 -> "Zrenjanin", 6 -> "Iasi", 7 -> "Lviv")

  def all: List[Location] = location.map(x => LocationImpl(x._1, x._2)).toList

  @throws(classOf[SystemError])
  def apply(id: Long) = find(_.id == id, s"Invalid location id $id")

  @throws(classOf[SystemError])
  def apply(name: String) = find(_.title.equalsIgnoreCase(name), s"Invalid location name $name")

  @throws(classOf[SystemError])
  private def find(p: Location => Boolean, msg: String) = all.filter(p) match {
    case Nil => throw SystemError(new Exception(msg))
    case x :: xs => x
  }
}