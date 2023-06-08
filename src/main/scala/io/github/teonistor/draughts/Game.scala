package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{GameState, Position, Settings}
import io.github.teonistor.draughts.rule.{AvailableMoves, AvailableMovesRule, GameOverChecker, PromotionRule}
import io.vavr.control.Validation
import io.vavr.control.Validation.{invalid, valid}

class Game(val availableMovesRule: AvailableMovesRule,
           val promotionRule: PromotionRule,
           val gameOverChecker: GameOverChecker,
           val settings: Settings, val gameState: GameState) {

  lazy val availableMoves: AvailableMoves = availableMovesRule.computeAvailableMoves(gameState, settings)
  lazy val isGameOver: Boolean = gameOverChecker.isGameOver(gameState, availableMoves)

  def move(from: Position, to: Position): Validation[String,Game] = {
   /* Caution! Visitor pattern looming as always!
    * Also, there's the whole Game State faff...
    *
    * For now let's assume there's always movement:
    * dig the map with suitable validations. If all maps, fine, return new Game with state containing other player and board which was dug
    * */

    availableMoves
      .get(from)
      .map(_.getOrElse(to, invalid(s"Your piece from $from cannot reach $to")))
      .getOrElse(invalid(s"You don't have a piece at $from"))
      .map(state => state.copy(board=promotionRule.promoteAsNeeded(state.board)))
      .map(newState => new Game(availableMovesRule, promotionRule, gameOverChecker, settings, newState))
  }

  def pass(): Validation[String, Game] = gameState.ongoingJump
    .map(_ => valid[String, Game](new Game(availableMovesRule, promotionRule, gameOverChecker, settings, GameState(gameState.board, gameState.currentPlayer.next, None))))
    .getOrElse(invalid("No jump in progress"))
}
