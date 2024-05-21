package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{GameState, Settings}
import io.github.teonistor.draughts.rule.{AvailableMovesRule, GameOverChecker, PromotionRule}

class InitialGameProvider(availableMovesRule: AvailableMovesRule, gameOverChecker: GameOverChecker, initialBoardProvider: InitialBoardProvider) {

  def createGame(settings: Settings) =
    new Game(availableMovesRule,
      new PromotionRule(settings),
      gameOverChecker,
      settings,
      GameState(
        initialBoardProvider.createBoard(settings),
        Player.white,
        None))
}
