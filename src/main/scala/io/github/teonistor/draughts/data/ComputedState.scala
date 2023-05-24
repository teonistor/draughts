package io.github.teonistor.draughts.data

import io.github.teonistor.draughts.Piece
import io.vavr.control.Validation

case class ComputedState(availableMoves: Map[Position, Map[Position, Validation[String, Map[Position, Piece]]]])
