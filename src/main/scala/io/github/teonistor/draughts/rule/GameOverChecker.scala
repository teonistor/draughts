package io.github.teonistor.draughts.rule

class GameOverChecker {

  def isGameOver(moves: AvailableMoves): Boolean =
    !moves
      .exists(_._2
        .exists(_._2.isValid))
}
