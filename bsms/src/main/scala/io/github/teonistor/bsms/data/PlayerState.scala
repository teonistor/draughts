package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.core.Orientation
import io.github.teonistor.bsms.rule.ShipPlacementRule
import io.vavr.control.Validation

case class PlayerState(board: OwnBoard,
                       opponentBoard: OpponentBoard,
                       shipsToPlace: Set[ShipDescription],
                       minesToPlace: Int) {

  def placeShip(ship: ShipDescription, position: Position, orientation: Orientation): Validation[String, PlayerState] =
    ShipPlacementRule.placeShip(board, ship, position, orientation)
      .map(b => copy(board=b))


}
