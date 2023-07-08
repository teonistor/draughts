package io.github.teonistor

package object draughts {

  implicit class VectorHasToFriendlyString(val vector: Vector[_]) extends AnyVal {
    def toFriendlyString:String = vector.mkString("(",",",")")
  }
}
