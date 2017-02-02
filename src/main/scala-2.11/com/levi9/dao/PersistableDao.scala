package com.levi9.dao

import com.levi9.domain.model.Id

import scala.concurrent.Future

protected[dao] trait PersistableDao[A, B] {
  def save(data: A): B
}

protected[dao] trait ItemByIdDao[A] {
  def findOne(id: Id): Future[Option[A]]
}