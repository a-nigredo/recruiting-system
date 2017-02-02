package com.levi9.common

sealed trait Error extends Exception

case class SystemError(ex: Throwable) extends Error

case class ValidationError(messages: List[String]) extends Error

case class NotFoundError(ex: Throwable) extends Error
