package com.levi9.dao

import com.levi9._

final case class Page[A](items: List[A], total: Int)

final case class Pageable(num: Int, size: Int) {
  def rangeFor(total: Int, defaultPage: Int = config.getInt("pagination.startPage")): Range = {
    if (this.size > 0 & this.num > 0 & total > this.size) {
      val totalPages = Math.ceil(total.toFloat / this.size.toFloat).toInt
      val from = this.size * (this.num - 1)
      val to = if (totalPages == this.num) total else this.size * this.num
      Range(from, to)
    } else {
      Range(defaultPage, total)
    }
  }
}

object Pageable {
  implicit def Pageable2Option(pageable: Pageable): Option[Pageable] = Some(pageable)
}

protected[dao] case class Range(from: Int, to: Int)