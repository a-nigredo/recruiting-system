package com.levi9.projection

import com.levi9.Difference
import com.levi9.domain.model.Storable

@Difference.ByField("id")
case class Seniority(id: Long, title: String) extends Storable[Long]
