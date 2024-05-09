package io.github.teonistor.connectn.data

import io.github.teonistor.connectn.Game
import io.github.teonistor.connectn.data.GameState.Board
import io.github.teonistor.draughts.HDUtils
import io.github.teonistor.draughts.HDUtils.cartesianProduct

object GameOverChecker {

  def isGameOver(game: Game): (Boolean, Option[Color]) = {

    // TODO 1x1 game??  LOL

    val current::previous::_ = game.history.map(_.board)

    val lastMove = current.keysIterator
      .find(k => !previous.contains(k) || current(k).size > previous(k).size)

    // Should never happen, but to be safe...
    if (lastMove.isEmpty)
      return (false, None)

    val lastColumn = lastMove.map(current).get
    val lastPosition = lastMove.get :+ lastColumn.size - 1
    if (current.forall(_._2.size >= game.settings.height))
      return (true, None)

    val existsWin = cartesianProduct(Vector.fill(game.settings.baseDimensions.size + 1)(Vector(-1, 0, 1))).iterator
      .filterNot(_.forall(_==0))
      .exists { delta =>
        Iterator.iterate(lastPosition.toList)(_.zip(delta).map { case (a, b) => a + b })
          .take(game.settings.winningThreshold)
          .map { case idx :: base =>
            current.getOrElse(base.to(Vector), List.empty).lift(idx)
          }
          .toSet
          .size == 1
      }

    if (existsWin)
      (true, Some(lastColumn.head))
    else
      (false, None)
  }
}
