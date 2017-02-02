package com.levi9.dao

import com.google.inject.AbstractModule
import com.levi9.dao.impl.hocon.HoconModule
import com.levi9.dao.impl.slick.SlickModule
import net.codingwell.scalaguice.ScalaModule

class GuiceModule extends AbstractModule with ScalaModule {

  override def configure() = {
    install(new SlickModule)
    install(new HoconModule)
  }
}