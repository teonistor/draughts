package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.Piece
import io.github.teonistor.draughts.data.{GameState, Position, Settings}
import io.github.teonistor.draughts.move.Move

class AvailableMovesRule {

  def computeAvailableMoves(gameState: GameState, settings: Settings): AvailableMoves = {

    val isPositionOnBoard = (p: Position) =>
      settings.boardSizes.indices.forall(i =>
        p(i) >= 0 && p(i) < settings.boardSizes(i))

    val executeMovesOnBoard = (from: Position, moves: Map[Position,Move]) => (from, moves.view
      .filterKeys(isPositionOnBoard)
      .mapValues(_ execute gameState)
      .toMap)

    val isCurrentPlayerAtPosition: ((_,Piece)) => Boolean = pp => gameState.currentPlayer.isMyPiece(pp._2)

    // TODO We've changed the behaviour of a TSNH edge case: "ongoing jump from empty position" used to block the game; now it ignores the ongoing. This is arguably better but UNTESTED

    gameState.ongoingJump
      .filter(sanityFilter(gameState.board.contains, "ongoing jump from empty position"))
      .map(pos => (pos, gameState.board(pos)))
      .filter(sanityFilter(isCurrentPlayerAtPosition, "ongoing jump by the noncurrent player"))
      .map { case (from, piece) => (from, piece emitJumps from) }
      .map(List(_))
      .getOrElse(gameState.board
        .filter(isCurrentPlayerAtPosition)
        .map { case (from, piece) => (from, piece emitMoves from) })
      .map(executeMovesOnBoard.tupled)
      .toMap
  }

  private def sanityFilter[T](pred:T=>Boolean, warn:String): T=>Boolean = thing => {
    if (pred(thing)) true
    else {
      System.err.println("Sanity check FAIL: " + warn)
      false
    }
  }
}
