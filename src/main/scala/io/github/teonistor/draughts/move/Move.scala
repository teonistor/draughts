package io.github.teonistor.draughts.move

import io.github.teonistor.draughts.data.Position
import io.github.teonistor.draughts.{Piece, Player}
import io.vavr.control.Validation
import io.vavr.control.Validation.{invalid, valid}

sealed trait Move {
  def execute(board: Map[Position, Piece]): Validation[String, Map[Position, Piece]]
}

object Move {

  case class Sliding(from: Position, to: Position) extends Move {
    override def execute(board: Map[Position, Piece]): Validation[String, Map[Position, Piece]] =
      if (board contains to)
        invalid(s"$to is occupied")
      else
        valid(board.updated(to, board(from)).removed(from))
  }

  case class Jumping(from: Position, over: Position, to: Position) extends Move {
    override def execute(board: Map[Position, Piece]): Validation[String, Map[Position, Piece]] =
      if (board contains to)
        invalid(s"$to is occupied")

      // TODO Duplication
      // TODO Bonkers way of doing it
      else if (board.get(over).exists(p => Player.white.isMyPiece(p) != Player.white.isMyPiece(board(from))))
        valid(board.updated(to, board(from)).removedAll(List(from, over)))

      else
        invalid(s"$over is not occupied by your opponent")
  }
}
