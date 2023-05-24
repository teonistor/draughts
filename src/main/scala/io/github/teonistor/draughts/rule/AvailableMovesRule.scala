package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.{Piece, Position, State}
import io.vavr.control.Validation

class AvailableMovesRule {

  def computeAvailableMoves(gameState: State): Map[Position, Map[Position, Validation[String, Map[Position, Piece]]]] = {
    gameState.board.view
      // .filter for my pieces
      .map {
        case (from, piece) => (from, piece.emitMoves(from)
          .view
          .filterKeys(isPositionOnBoard)
          .mapValues(_.execute(gameState.board))
          .toMap)
      }
      .filterNot(_._2.isEmpty)
      .toMap
  }

  // Not all cases currently tested, but eventually this logic will be relegated to the board and/or settings telling us how big the board actually is
  private def isPositionOnBoard(p: Position) =
    p.x >= 0 && p.x < 8 && p.y >= 0 && p.y < 8
}
