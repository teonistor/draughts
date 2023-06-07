package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.Position
import io.github.teonistor.draughts.move.Move

sealed trait Piece {
  def emitMoves(from:Position): Map[Position, Move]
  def emitJumps(from:Position): Map[Position, Move]
  def promote: Piece
}

object Piece {
  private val `+-1` = List(1, -1)

  // DONTDO Ideally the concept of "forward" would be relegated to the board (via the specific player) but the static initialisaiton implications of that are asinine

  val blackKing:Piece = new King
  val blackPeon:Piece = new Peon(-1, blackKing)
  val whiteKing:Piece = new King
  val whitePeon:Piece = new Peon(+1, whiteKing)

  private trait PieceBase {
    def emitMoves(from: Position): Map[Position, Move] =
      emitMovesWith(emitMovesInOneDirection(from, _, _, emitAllMovesInOneDirection))

    def emitJumps(from: Position): Map[Position, Move] =
      emitMovesWith(emitMovesInOneDirection(from, _, _, emitJumpsInOneDirection))

    protected def emitMovesWith(emission: (Int, Int) => IterableOnce[(Position,Move)]): Map[Position, Move]
  }

  private class King extends Piece with PieceBase {
    protected def emitMovesWith(emission: (Int, Int) => IterableOnce[(Position,Move)]): Map[Position, Move] =
      `+-1`.flatMap(dx =>
        `+-1`.flatMap(dy =>
          emission(dx, dy))).toMap

    override def promote: Piece = this
  }

  private class Peon(forward: Int, val promote: Piece) extends Piece with PieceBase {
    protected def emitMovesWith(emission: (Int, Int) => IterableOnce[(Position, Move)]): Map[Position, Move] =
      `+-1`.flatMap(dx => emission(dx, forward)).toMap
  }

  private def emitMovesInOneDirection(from: Position, dx: Int, dy: Int, emission: (Position,Position,Position)=>IterableOnce[(Position,Move)]) = {
    val x1 = dx + from.x
    val y1 = dy + from.y
    emission(from, Position(x1, y1), Position(dx + x1, dy + y1))
  }

  private def emitAllMovesInOneDirection(from: Position, p1: Position, p2: Position) =
    List(p1 -> Move.Sliding(from, p1),
         p2 -> Move.Jumping(from, p1, p2))

  private def emitJumpsInOneDirection(from: Position, p1: Position, p2: Position) =
    Some(p2 -> Move.Jumping(from, p1, p2))
}
