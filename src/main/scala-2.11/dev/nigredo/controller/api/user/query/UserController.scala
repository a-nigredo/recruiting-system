package com.levi9.controller.api.user.query

import akka.http.scaladsl.server.Directives._
import com.levi9.controller.Directives._
import com.levi9.controller.SimpleController
import com.levi9.controller.api.JsonSupport
import com.levi9.dao.Pageable
import com.levi9.dao.Query.{PageableQueryUserDao, QueryRoleDao}
import com.levi9.dao.filter.dsl.User.UserId
import com.levi9.representation.json.Query.PageableResponse
import com.levi9.representation.json.Query._

trait UserController
  extends JsonSupport
    with SimpleController {

  val queryUserDao: PageableQueryUserDao
  val queryRoleDao: QueryRoleDao

  override protected def buildRoutes() = {

    def roleList = (pathPrefix("role" / "type") & get & pathEnd) (onSuccess(queryRoleDao.findAll())(x => Ok(x.toJson)))

    def list = (get & pathEnd & withPagination) { (pageNum, perPage) =>
      onSuccess(queryUserDao.findAll(None)(Pageable(pageNum, perPage)))(result => Ok(PageableResponse(result.items, result.total).toJson))
    }

    pathPrefix("users") {
      list ~ roleList ~ (pathPrefix(LongNumber) & pathEnd & get) (id => onSuccess(queryUserDao.findOne(UserId == id))(_.map(user => Ok(user.role.toJson)).getOrElse(NotFound)))
    }
  }
}
