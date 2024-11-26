package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.core.BattleshipMinesweeper

object StageChange {

  def advanceStageIfNecessary(game: BattleshipMinesweeper): BattleshipMinesweeper =
    if (game.aliceState.isStageOver && game.bobState.isStageOver)
      game.copy(aliceState=game.aliceState.nextStage(game.settings), bobState=game.bobState.nextStage(game.settings))
    else
      game
}
