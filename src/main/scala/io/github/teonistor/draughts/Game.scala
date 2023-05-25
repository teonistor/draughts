package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{ComputedState, GameState, Position, Settings}
import io.github.teonistor.draughts.rule.AvailableMovesRule
import io.vavr.control.Validation
import io.vavr.control.Validation.invalid

class Game(val availableMovesRule: AvailableMovesRule,
           val settings: Settings, val gameState: GameState) {

  lazy val computedState: ComputedState = ComputedState(
    availableMovesRule computeAvailableMoves gameState)

  def move(from: Position, to: Position): Validation[String,Game] = {
   /* Caution! Visitor pattern looming as always!
    * Also, there's the whole Game State faff...
    *
    * For now let's assume there's always movement:
    * dig the map with suitable validations. If all maps, fine, return new Game with state containing other player and board which was dug
    * */

    computedState.availableMoves
      .get(from)
      .map(_.getOrElse(to, invalid(s"Your piece from $from cannot reach $to")))
      .getOrElse(invalid(s"You don't have a piece at $from"))
    // Here be logic to keep the same player if jump
      .map(newBoard => GameState(newBoard, gameState.currentPlayer.next))
      .map(newState => new Game(availableMovesRule, settings, newState))
  }

}
