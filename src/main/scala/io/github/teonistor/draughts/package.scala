package io.github.teonistor

import io.github.teonistor.commongaming.{GamesHolder, HyperView}
import io.github.teonistor.draughts.data.Settings

package object draughts {

  implicit class VectorHasToFriendlyString(val vector: Vector[_]) extends AnyVal {
    def toFriendlyString:String = vector.mkString("(",",",")")
  }

  type JunctureFactory = (String, HyperView[Game]) => Juncture
  type GamesHolderFactory = HyperView[Game] => GamesHolder[Game,Settings]
}
