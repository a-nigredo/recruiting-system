package com.levi9.projection

import com.levi9.Difference
import com.levi9.projection.Common.Author

case class History(id: String, data: List[Difference], author: Author, creationDate: Long)
