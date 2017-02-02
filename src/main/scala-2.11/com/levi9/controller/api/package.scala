package com.levi9.controller

import com.levi9.projection.Source

package object api extends JsonSupport {

  implicit val candidateSourceTypeFormat = jsonFormat2(Source)
}
