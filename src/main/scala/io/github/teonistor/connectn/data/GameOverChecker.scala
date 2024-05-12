package io.github.teonistor.connectn.data

import io.github.teonistor.connectn.Game
import io.github.teonistor.connectn.data.GameState.Board
import io.github.teonistor.draughts.HDUtils.cartesianProduct

import scala.collection.Iterator.iterate

object GameOverChecker {

  def isGameOver(game: Game): (Boolean, Option[Color]) =
    game.history.map(_.board) match {

      case current :: previous :: _=>
        val lastMove = current.keysIterator
          .find(k => !previous.contains(k) || current(k).size > previous(k).size)

        // Should never happen, but to be safe...
        if (lastMove.isEmpty)
          return (false, None)

        val lastColumn = lastMove.map(current).get
        val lastPosition = lastMove.get :+ lastColumn.size - 1

        if (current.forall(_._2.size >= game.settings.height))
          (true, None)

        else if (cartesianProduct(Vector.fill(game.settings.baseDimensions.size + 1)(Vector(-1, 0, 1))).iterator
            .filterNot(_.forall(_ == 0))
            .exists(hasStreak(current, lastPosition, _, game.settings.winningThreshold)))
          (true, Some(lastColumn.head))

        else
          (false, None)

      // No history. We assume that means nobody won yet, because I said so.
      case _=> (false, None)
    }

  private def hasStreak(board: Board, position: Vector[Int], direction: Vector[Int], winningThreshold: Int) =
    iterate(position.toList)(_.zip(direction).map(addPair))
      .take(winningThreshold)
      .map { case idx :: base =>
        board.getOrElse(base.to(Vector), List.empty).lift(idx) }
      .toSet
      .size == 1

  private def addPair(pair:(Int,Int)) =
    pair._1 + pair._2
}
