package com.levi9.dao

import com.levi9.dao.filter.Filter

/**
  * Abstraction for querying list of entities.
  *
  * @tparam A - entity type
  */
protected[dao] trait ListDao[A] {

  def findAll(filter: Option[Filter] = None): A

}
