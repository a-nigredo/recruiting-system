package com.levi9.projection

import com.levi9.Acl

case class Auth(token: String, username: String, acl: Option[Acl])