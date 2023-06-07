package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{GameState, Position}
import io.vavr.control.Validation

package object rule {
  type AvailableMoves = Map[Position, Map[Position, Validation[String, GameState]]]
}
