package com.levi9.dao

import com.levi9.dao.filter.Filter

/**
  * Abstraction for querying single entity instance
  *
  * @tparam A - entity type
  */
protected[dao] trait ItemDao[A] {

  def findOne(filter: Filter): A

}
