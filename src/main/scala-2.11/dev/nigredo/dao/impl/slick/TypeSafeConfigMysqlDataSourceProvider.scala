package com.levi9.dao.impl.slick

import com.levi9._
import com.levi9.dao.impl.slick.common.JDBCDataSourceProvider
import slick.driver.MySQLDriver
import slick.jdbc.JdbcBackend._

protected[slick] class TypeSafeConfigMysqlDataSourceProvider(val configName: String) extends JDBCDataSourceProvider {
  override val profile = MySQLDriver
  override val source = Database.forConfig(configName, config = config)
}