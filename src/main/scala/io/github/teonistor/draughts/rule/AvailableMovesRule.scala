package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.data.{GameState, Position, Settings}

class AvailableMovesRule {

  def computeAvailableMoves(gameState: GameState, settings: Settings): AvailableMoves = {

    def isPositionOnBoard(p: Position) =
      p.x >= 0 && p.x < settings.boardWidth && p.y >= 0 && p.y < settings.boardHeight

    gameState.board.view
      .filter { case (_, piece) => gameState.currentPlayer isMyPiece piece }
      .map { case (from, piece) => (from, piece.emitMoves(from)
        .view
        .filterKeys(isPositionOnBoard)
        .mapValues(_ execute gameState.board)
        .toMap) }
      .filterNot(_._2.isEmpty)
      .toMap
  }
}
