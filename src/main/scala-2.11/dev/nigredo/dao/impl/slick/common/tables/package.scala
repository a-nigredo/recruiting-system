package com.levi9.dao.impl.slick.common

import java.sql.Date

package object tables {
  implicit def Date2SQLTimestamp(date: java.util.Date): Date = new Date(date.getTime)
}
