package io.github.teonistor.draughts.move

import io.github.teonistor.draughts.data.GameState
import io.github.teonistor.draughts.{Piece, Player, VectorHasToFriendlyString}
import io.vavr.control.Validation
import io.vavr.control.Validation.{invalid, valid}

sealed trait Move {
  def execute(gameState: GameState): Validation[String, GameState]
}

object Move {

  case class Sliding(from: Vector[Int], to: Vector[Int]) extends Move {
    override def execute(gameState: GameState): Validation[String, GameState] =
      validateDestination(gameState, to, valid(GameState(
        gameState.board.updated(to, gameState.board(from)).removed(from),
        gameState.currentPlayer.next,
        None)))
  }

  case class Jumping(from: Vector[Int], over: Vector[Int], to: Vector[Int]) extends Move {
    override def execute(gameState: GameState): Validation[String, GameState] =
      validateDestination(gameState, to,

        if (capturable(gameState.board))
          valid(GameState(gameState.board.updated(to, gameState.board(from)).removedAll(List(from, over)), gameState.currentPlayer, Some(to)))

        else
          invalid(s"${over.toFriendlyString} is not occupied by your opponent"))

    private def capturable(board: Map[Vector[Int], Piece]) =
      board.get(over).exists(Player.white.isMyPiece(_) != Player.white.isMyPiece(board(from)))
  }

  private def validateDestination(gameState: GameState, to: Vector[Int], validCase: => Validation[String,GameState]): Validation[String,GameState] =
    if (gameState.board contains to)
      invalid(s"${to.toFriendlyString} is occupied")
    else validCase
}
