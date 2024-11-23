package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.core.Orientation
import io.github.teonistor.bsms.data.OceanCell.{damagedShip, mine}
import io.github.teonistor.bsms.rule.ShipPlacementRule
import io.vavr.control.Validation
import io.vavr.control.Validation.{invalid, valid}

case class PlayerState(board: OwnBoard,
                       opponentBoard: OpponentBoard,
                       shipsToPlace: Set[ShipDescription],
                       minesToPlace: Int,
                       moveToMake: Boolean,
                       shotToShoot: Boolean) {

  def placeShip(ship: ShipDescription, position: Position, orientation: Orientation): Validation[String, PlayerState] =
    ShipPlacementRule.placeShip(board, ship, position, orientation)
      .map(b => copy(board = b))

  def moveShip(position: Position, movement: Vector[Int]): Validation[String, PlayerState] =
    ShipPlacementRule.moveShip(board, position, movement)
      .map(b => copy(board = b))

  def useShip(ship: ShipDescription): Validation[String, PlayerState] =
    if (shipsToPlace.contains(ship))
      valid(copy(shipsToPlace = shipsToPlace - ship))
    else
      invalid("Cannot use a ship you do not have")

  def placeMine(position: Position): PlayerState =
    copy(board = board ++ board
      .get(position)
      .flatMap(_.toOption)
      .map(ship => ship.copy(parts = ship.parts + (position -> damagedShip)))
      .map(ship => ship.parts.keySet.map((_, Right(ship))))
      .getOrElse(Some(position -> Left(mine))))

  def useMine(): Validation[String, PlayerState] =
    if (minesToPlace > 0)
      valid(copy(minesToPlace = minesToPlace - 1))
    else
      invalid("Cannot use a mine you do not have")

  def move(): Validation[String, PlayerState] =
    if (moveToMake)
      valid(copy(moveToMake = false))
    else
      invalid("You do not have a move available")

  def shoot(): Validation[String, PlayerState] =
    if (shotToShoot)
      valid(copy(shotToShoot = false))
    else
      invalid("You do not have a shot available")
}
