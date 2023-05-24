package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{ComputedState, GameState, Position, Settings}
import io.github.teonistor.draughts.rule.AvailableMovesRule
import io.vavr.control.Validation

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


    null
  }

}
