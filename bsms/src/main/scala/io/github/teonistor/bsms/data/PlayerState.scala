package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.core.Orientation
import io.github.teonistor.bsms.data.OceanCell.{damagedShip, mine}
import io.github.teonistor.bsms.rule.ShipPlacementRule
import io.vavr.control.Validation
import io.vavr.control.Validation.{invalid, valid}

trait PlayerState {

  val board: OwnBoard

  def withBoard(board: OwnBoard): PlayerState

  def useShip(ship: ShipDescription): Validation[String,PlayerState] =
    invalid("Cannot use ship outside ship placement stage")

  def useMine(): Validation[String,PlayerState] =
    invalid("Cannot use mine outside mine placement stage")

  def moveShip(position: Position, movement: Vector[Int]): Validation[String, PlayerState] =
    invalid("Cannot move ship outside ship movement stage")

  def shoot(): Validation[String,PlayerState] =
    invalid("Cannot shoot outside shooting stage")

  def placeShip(ship: ShipDescription, position: Position, orientation: Orientation): Validation[String, PlayerState] =
    ShipPlacementRule.placeShip(board, ship, position, orientation)
      .map(withBoard)

  def placeMine(position: Position): PlayerState =
    withBoard(board ++ board
      .get(position)
      .flatMap(_.toOption)
      .map(ship => ship.copy(parts = ship.parts + (position -> damagedShip)))
      .map(ship => ship.parts.keySet.map((_, Right(ship))))
      .getOrElse(Some(position -> Left(mine))))

  def isStageOver: Boolean
}

case class PlayerStateShipPlacement(board: OwnBoard,
                                    opponentBoard: OpponentBoard,
                                    shipsToPlace: Set[ShipDescription]) extends PlayerState {

  override def useShip(ship: ShipDescription): Validation[String, PlayerState] =
    if (shipsToPlace.contains(ship))
      valid(copy(shipsToPlace = shipsToPlace - ship))
    else
      invalid("Cannot use a ship you do not have")

  override def withBoard(board: OwnBoard): PlayerState = copy(board = board)

  lazy val isStageOver: Boolean = shipsToPlace.isEmpty
}

case class PlayerStateMinePlacement(board: OwnBoard,
                                    opponentBoard: OpponentBoard,
                                    minesToPlace: Int) extends PlayerState {

  override def useMine(): Validation[String, PlayerState] =
    if (minesToPlace > 0)
      valid(copy(minesToPlace = minesToPlace - 1))
    else
      invalid("Cannot use a mine you do not have")

  override def withBoard(board: OwnBoard): PlayerState = copy(board = board)

  lazy val isStageOver: Boolean = minesToPlace <= 0
}

case class PlayerStateMovement(board: OwnBoard,
                               opponentBoard: OpponentBoard,
                               moveToMake: Boolean) extends PlayerState {

  override def moveShip(position: Position, movement: Vector[Int]): Validation[String, PlayerState] =
    if (moveToMake)
      ShipPlacementRule.moveShip(board, position, movement)
        .map(b => copy(board = b, moveToMake = false))
    else
      invalid("You do not have a move available")

  override def withBoard(board: OwnBoard): PlayerState = copy(board = board)

  lazy val isStageOver: Boolean = !moveToMake
}

case class PlayerStateShooting(board: OwnBoard,
                               opponentBoard: OpponentBoard,
                               shotToShoot: Boolean) extends PlayerState {

  override def shoot(): Validation[String, PlayerState] =
    if (shotToShoot)
      valid(copy(shotToShoot = false))
    else
      invalid("You do not have a shot available")

  override def withBoard(board: OwnBoard): PlayerState = copy(board = board)

  lazy val isStageOver: Boolean = !shotToShoot
}
