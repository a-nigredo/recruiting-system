package com.levi9.dao.impl.hocon

import com.google.inject.AbstractModule
import com.levi9.dao.impl.hocon.query.HoconConfigDao
import com.levi9.dao.Query.QueryConfigDao
import net.codingwell.scalaguice.ScalaModule

class HoconModule
  extends AbstractModule
    with ScalaModule {

  def configure() = {
    bind[QueryConfigDao].toInstance(new HoconConfigDao {})
  }
}