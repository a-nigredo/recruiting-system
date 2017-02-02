package com.levi9.dao.impl.slick.common

import com.levi9.dao.DataSourceProvider
import slick.driver.JdbcDriver
import slick.jdbc.JdbcBackend._

trait Profile {
  val profile: JdbcDriver
}

trait JDBCDataSourceProvider extends Profile with DataSourceProvider[Database]