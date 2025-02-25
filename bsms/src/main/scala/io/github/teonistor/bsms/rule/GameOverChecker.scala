package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.core.{BattleshipMinesweeper, PlayerState, PlayerStateMovement, PlayerStateShooting}
import io.github.teonistor.bsms.data.GameCondition
import io.github.teonistor.bsms.data.GameCondition.{aliceWins, bobWins, continues, everyoneLoses}
import io.github.teonistor.bsms.data.OceanCell.healthyShip

object GameOverChecker {

  def check(game: BattleshipMinesweeper): GameCondition =
    if (isTerminable(game.aliceState) && isTerminable(game.bobState))
      (doesSurvive(game.aliceState), doesSurvive(game.bobState)) match {
        case (true, true) => continues
        case (true, false) => aliceWins
        case (false, true) => bobWins
        case _=> everyoneLoses
      }
    else
      continues

  private def isTerminable(state: PlayerState) =
    state.isInstanceOf[PlayerStateMovement] || state.isInstanceOf[PlayerStateShooting]

  private def doesSurvive(state: PlayerState) =
    state.board.exists(_._2 match {
      case Right(ship) => ship.parts.exists(_._2 == healthyShip)
      case _ => false
    })
}
