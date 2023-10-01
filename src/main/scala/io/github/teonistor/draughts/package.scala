package io.github.teonistor

import io.github.teonistor.commongaming.HyperView

package object draughts {

  implicit class VectorHasToFriendlyString(val vector: Vector[_]) extends AnyVal {
    def toFriendlyString:String = vector.mkString("(",",",")")
  }

  type JunctureFactory = (String, HyperView[Game]) => Juncture
}
