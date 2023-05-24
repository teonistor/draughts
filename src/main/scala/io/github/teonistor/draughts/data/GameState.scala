package io.github.teonistor.draughts.data

import io.github.teonistor.draughts.{Piece, Player}

case class GameState(board: Map[Position, Piece],
                     currentPlayer: Player)
