package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.Piece
import io.github.teonistor.draughts.data.{GameState, Position, Settings}

class AvailableMovesRule {

  def computeAvailableMoves(gameState: GameState, settings: Settings): AvailableMoves = {

    val isPositionOnBoard = (p: Position) =>
      p.x >= 0 && p.x < settings.boardWidth && p.y >= 0 && p.y < settings.boardHeight

    val arePositionAndPieceRelevant = (position: Position, piece: Piece) =>
      // Now we have a misrepresentation problem because invalidity messages from the game would imply these pieces won't exist
      !gameState.ongoingJump.exists(_ != position) &&
       gameState.currentPlayer.isMyPiece(piece)

    val emitMoves = (from: Position, piece: Piece) => (from, piece.emitMoves(from).view
      .filterKeys(isPositionOnBoard)
      .mapValues(_ execute gameState.board)
      .toMap)

    gameState.board.view
      .filter(arePositionAndPieceRelevant.tupled)
      .map(emitMoves.tupled)
      .filterNot(_._2.isEmpty)  // Ensure no sources are returned with empty targets... for sanity
      .toMap
  }
}
