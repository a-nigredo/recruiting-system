package com.levi9

import scala.annotation.tailrec

package object dto {

  trait Updatable {
    this: Product =>
    def isChanged: Boolean = {
      @tailrec
      def run(start: Int): Boolean = {
        if (start > this.productArity - 1) false
        else if (this.productElement(start).isInstanceOf[Some[_]]) true
        else run(start + 1)
      }

      run(0)
    }
  }

}
