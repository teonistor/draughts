package io.github.teonistor.draughts.data

import io.github.teonistor.draughts.{Piece, Player}

/** The state of the game
 * @param board mapping nonempty position to the piece they are occupied by
 * @param currentPlayer player about to make a move
 * @param ongoingJump if the last move to happen was a jump by the current player, the position onto which that piece landed; otherwise None
 */
case class GameState(board: Map[Position, Piece],
                     currentPlayer: Player,
                     ongoingJump: Option[Position])
