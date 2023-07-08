package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.GameState
import io.vavr.control.Validation

package object rule {
  type AvailableMoves = Map[Vector[Int], Map[Vector[Int], Validation[String, GameState]]]
}
