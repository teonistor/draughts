package io.github.teonistor.draughts.move

import io.github.teonistor.draughts.data.{GameState, Position}
import io.github.teonistor.draughts.{Piece, Player}
import io.vavr.control.Validation
import io.vavr.control.Validation.{invalid, valid}

sealed trait Move {
  def execute(gameState: GameState): Validation[String, GameState]
}

object Move {

  case class Sliding(from: Position, to: Position) extends Move {
    override def execute(gameState: GameState): Validation[String, GameState] =
      validateDestination(gameState, to, valid(GameState(
        gameState.board.updated(to, gameState.board(from)).removed(from),
        gameState.currentPlayer.next,
        None)))
  }

  case class Jumping(from: Position, over: Position, to: Position) extends Move {
    override def execute(gameState: GameState): Validation[String, GameState] =
      validateDestination(gameState, to,
        if (capturable(gameState.board))
          valid(GameState(gameState.board.updated(to, gameState.board(from)).removedAll(List(from, over)), gameState.currentPlayer, Some(to)))

        else
          invalid(s"$over is not occupied by your opponent"))

//        if (board contains to)
//        invalid(s"$to is occupied")
//
//      // TODO Duplication
//      // TODO Bonkers way of doing it
//      else if (board.get(over).exists(p => Player.white.isMyPiece(p) != Player.white.isMyPiece(board(from))))
//        valid(board.updated(to, board(from)).removedAll(List(from, over)))

//      else
//        invalid(s"$over is not occupied by your opponent")

    private def capturable(board: Map[Position, Piece]) =
      board.get(over).exists(Player.white.isMyPiece(_) != Player.white.isMyPiece(board(from)))
  }

  private def validateDestination(gameState: GameState, to: Position, validCase: => Validation[String,GameState]): Validation[String,GameState] =
    if (gameState.board contains to)
      invalid(s"$to is occupied")
    else validCase
}
