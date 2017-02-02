package com.levi9

import com.levi9.projection.Location
import com.levi9.security.PrivilegesView

case class Config(cv: CvConf, location: Location, security: Security)

case class CvConf(allowedFiles: List[String])

case class Security(roles: List[String], defaultRole: String, acl: List[Acl])

case class Acl(role: String, resources: List[Resource], privileges: List[PrivilegesView.Privilege])

case class Resource(methods: List[String], urls: List[String])
