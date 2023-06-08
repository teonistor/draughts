package io.github.teonistor.draughts.rule
import io.github.teonistor.draughts.data.GameState

class GameOverChecker {

  def isGameOver(state: GameState, moves: AvailableMoves): Boolean =
    !(state.ongoingJump.isDefined
      || moves
        .exists(_._2
          .exists(_._2.isValid)))
}
