package com.levi9.dao.impl.hocon.query

import com.levi9._
import com.levi9.common.SystemError
import com.levi9.dao.Query.ConfigDao
import com.levi9.domain.model.Candidates._
import com.levi9.projection.Location
import com.levi9.security.PrivilegesView

import scala.collection.JavaConversions._

trait HoconConfigDao
  extends ConfigDao {

  @throws(classOf[SystemError])
  override def apply(location: Location) = applicationConfig.collectFirst {
    case office if office.contains(location.title.toLowerCase) => office.get(location.title.toLowerCase)
  }.flatten match {
    case None => throw SystemError(new Exception(s"Configuration for location '${location.title}' doesn't exist"))
    case Some(config) => config
  }

  private val applicationConfig = {
    officesConfig.map(_.getString("location")).map { location =>
      Map(location.toLowerCase -> officesConfig.collectFirst {
        case office if office.getString("location").equalsIgnoreCase(location) =>
          val cv = CvConf(office.getStringList("cv.allowedFiles").toList)
          val aclConfig = office.getConfig("security.acl")
          val acl = aclConfig.entrySet().map { x =>
            val rules = aclConfig.getConfigList(x.getKey)
              .map(x => Resource(x.getStringList("method").toList, x.getStringList("url").toList)).toList
            Acl(x.getKey, rules, implicitly[PrivilegesView[CandidateDomain]].apply(x.getKey))
          }.toList
          val security = Security(office.getStringList("security.roles").toList,
            office.getString("security.defaultRole"), acl)
          Config(cv, Location(location), security)
      }.get)
    }
  }
}

