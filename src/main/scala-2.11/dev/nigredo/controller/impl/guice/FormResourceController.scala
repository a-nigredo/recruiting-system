package com.levi9.controller.impl.guice

import javax.inject.Inject

import com.google.inject.name.Named
import com.levi9.controller.api.VocabularyQueryController
import com.levi9.controller.{SecuredController, SimpleController}
import com.levi9.dao.Query.{QueryPositionDao, QuerySeniorityDao, QuerySourceTypeDao}

class FormResourceController @Inject()(@Named("SlickQuerySeniorityDao") val seniorityDao: QuerySeniorityDao,
                                       @Named("SlickQueryPositionDao") val positionDao: QueryPositionDao,
                                       @Named("SlickQuerySourceTypeDao") val sourceTypeDao: QuerySourceTypeDao)
  extends VocabularyQueryController with SimpleController with SecuredController {

  override def buildRoutes() = vocabulary
}
