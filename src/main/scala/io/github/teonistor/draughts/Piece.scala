package io.github.teonistor.draughts

import io.github.teonistor.draughts.move.Move

sealed trait Piece {
  def emitMoves(from: Position): Map[Position, Move]
  def promote: Piece
}

object Piece {
  private val `+-1` = LazyList(1, -1)

  // TODO Ideally the concept of "forward" would be relegated to the board (via the specific player) but the static initialisaiton implications of that are asinine

  val blackKing:Piece = new King
  val blackPeon:Piece = new Peon(_-1, blackKing)
  val whiteKing:Piece = new King
  val whitePeon:Piece = new Peon(_+1, whiteKing)

  private class King extends Piece {
    override def emitMoves(from: Position): Map[Position, Move] =
      `+-1`.flatMap(dx =>
        `+-1`.flatMap(dy =>
          emitMovesInOneDirection(from, dx+_, dy+_))).toMap

    override def promote: Piece = this
  }

  private class Peon(forward: Int=>Int, val promote: Piece) extends Piece {
    override def emitMoves(from: Position): Map[Position, Move] =
      `+-1`.flatMap(dx =>
        emitMovesInOneDirection(from, dx+_, forward)).toMap
  }

  private def emitMovesInOneDirection(from: Position, dx: Int=>Int, dy: Int=>Int) = {
    val x1 = dx(from.x)
    val y1 = dy(from.y)
    val p1 = Position(x1, y1)
    val p2 = Position(dx(x1), dy(y1))
    List(
      p1 -> Move.Sliding(from, p1),
      p2 -> Move.Jumping(from, p1, p2))
  }
}
