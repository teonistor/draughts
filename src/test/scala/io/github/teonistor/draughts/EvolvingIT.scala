package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{GameState, Settings}
import io.github.teonistor.draughts.rule.{AvailableMovesRule, GameOverChecker, PromotionRule}
import org.scalatest.funsuite.AnyFunSuite

object EvolvingIT extends AnyFunSuite{

  def main(arg: Array[String]): Unit =
    new TerminalInput(new Juncture(createGame, new TerminalView(System.out)), System.in, System.out).run()

  // TODO Make this an InitialGameProvider
  private def createGame(settings: Settings) =
    new Game(new AvailableMovesRule(),
      new PromotionRule(settings),
      new GameOverChecker(),
      settings,
      GameState(
        new InitialBoardProvider().createBoard(settings),
        Player.white,
        None))
}
