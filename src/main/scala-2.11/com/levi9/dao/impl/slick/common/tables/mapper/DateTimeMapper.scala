package com.levi9.dao.impl.slick.common.tables.mapper

import java.sql.Timestamp
import java.util.Date

import com.levi9.dao.impl.slick.common.Profile

protected[tables] trait DateTimeMapper extends Profile {

  import profile.api._

  lazy implicit val Date2Timestamp = MappedColumnType.base[Date, Timestamp]({
    utilDate => new java.sql.Timestamp(utilDate.getTime)
  }, {
    sqlTimestamp => new java.util.Date(sqlTimestamp.getTime)
  })

}
