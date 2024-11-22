package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.core.Orientation
import io.github.teonistor.bsms.rule.ShipPlacementRule
import io.vavr.control.Validation
import io.vavr.control.Validation.{invalid, valid}

case class PlayerState(board: OwnBoard,
                       opponentBoard: OpponentBoard,
                       shipsToPlace: Set[ShipDescription],
                       minesToPlace: Int) {

  def placeShip(ship: ShipDescription, position: Position, orientation: Orientation): Validation[String, PlayerState] =
    ShipPlacementRule.placeShip(board, ship, position, orientation)
      .map(b => copy(board=b))

  def useShip(ship: ShipDescription): Validation[String, PlayerState] =
    if (shipsToPlace.contains(ship))
      valid(copy(shipsToPlace = shipsToPlace - ship))
    else
      invalid("Cannot use a ship you do not have")

  def useMine(): Validation[String, PlayerState] =
    if (minesToPlace > 0)
      valid(copy(minesToPlace = minesToPlace - 1))
    else
      invalid("Cannot use a ship you do not have")
}
