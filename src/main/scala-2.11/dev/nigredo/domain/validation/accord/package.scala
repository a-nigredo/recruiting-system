package com.levi9.domain.validation

import com.wix.accord.Descriptions.Explicit
import com.wix.accord.{Failure, Result}

package object accord {

  implicit def Failure2ErrorList(result: Result): List[String] = result match {
    case Failure(errors) => errors.filter(_.description match {
      case Explicit(_) => true
      case _ => false
    }).map(x => x.description match {
      case Explicit(desc) => desc
      case _ => ""
    }).toList
    case _ => Nil
  }
}