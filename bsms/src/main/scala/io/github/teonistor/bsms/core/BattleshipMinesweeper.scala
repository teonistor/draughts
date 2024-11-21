package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.data._
import io.github.teonistor.bsms.rule.ShipPlacementRule

class BattleshipMinesweeper(playerState: PlayerState) {

  def placeShip(player: Int, ship: ShipDescription, position: Position, orientation: Orientation): ValidatedGame =
    ShipPlacementRule.placeShip(playerState.board, ship, position, orientation )
      .map(newBoard => playerState.copy(board=newBoard))
      .map(new BattleshipMinesweeper(_))

  def inspect(player: Int): OwnBoard =
    playerState.board

}
