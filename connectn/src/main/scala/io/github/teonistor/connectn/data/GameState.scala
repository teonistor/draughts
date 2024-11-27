package io.github.teonistor.connectn.data

case class GameState(board: GameState.Board,
                     currentPlayer: Color)

object GameState {
  type Board = Map[Vector[Int], List[Color]]
}
