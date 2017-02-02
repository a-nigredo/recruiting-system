package com.levi9.dao.impl.slick.common.tables.mapper

import com.levi9.dao.impl.slick.common.Profile
import com.levi9.domain.model.Id

protected[tables] trait IdMapper extends Profile {

  import profile.api._

  lazy implicit val Id2Value = MappedColumnType.base[Id, String]({ id => id.value }, { value => Id(value) })

}
