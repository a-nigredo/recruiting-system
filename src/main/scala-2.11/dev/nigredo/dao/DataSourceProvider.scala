package com.levi9.dao

protected[dao] trait DataSourceProvider[T] {
  val source: T
}