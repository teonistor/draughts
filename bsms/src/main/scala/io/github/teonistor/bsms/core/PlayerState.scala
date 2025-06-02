package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.data.OceanCell.{damagedShip, mine}
import io.github.teonistor.bsms.data._
import io.github.teonistor.bsms.rule.ShipPlacementRule
import io.vavr.control.Validation.{invalid, valid}

trait PlayerState {

  val board: OwnBoard

  def withBoard(board: OwnBoard): PlayerState

  def useShip(ship: ShipDescription): ValidatedState =
    invalid("Cannot use ship outside ship placement stage")

  def useMine(): ValidatedState =
    invalid("Cannot use mine outside mine placement stage")

  def moveShip(position: Position, movement: Movement): ValidatedState =
    invalid("Cannot move ship outside ship movement stage")

  def shoot(): ValidatedState =
    invalid("Cannot shoot outside shooting stage")

  def pass(): PlayerState = this

  def placeShip(ship: ShipDescription, position: Position, orientation: Orientation): ValidatedState =
    ShipPlacementRule.placeShip(board, ship, position, orientation)
      .map(withBoard)

  def placeMine(position: Position): PlayerState =
    withBoard(board ++ board
      .get(position)
      .flatMap(_.toOption)
      .map(ship => ship.copy(parts = ship.parts + (position -> damagedShip)))
      .map(ship => ship.parts.keySet.map((_, Right(ship))))
      .getOrElse(Some(position -> Left(mine))))

  def removeShip(position: Position): PlayerState =
    withBoard(ShipPlacementRule.isolateShip(board, position)._2)

  def isStageOver: Boolean

  def nextStage(gameSettings: GameSettings): PlayerState
}

case class PlayerStateShipPlacement(board: OwnBoard,
                                    opponentBoard: OpponentBoard,
                                    shipsToPlace: Set[ShipDescription]) extends PlayerState {

  override def useShip(ship: ShipDescription): ValidatedState =
    if (shipsToPlace.contains(ship))
      valid(copy(shipsToPlace = shipsToPlace - ship))
    else
      invalid("Cannot use a ship you do not have")

  override def withBoard(board: OwnBoard): PlayerState = copy(board = board)

  lazy val isStageOver: Boolean = shipsToPlace.isEmpty

  override def nextStage(gameSettings: GameSettings): PlayerState =
    PlayerStateMinePlacement(board, opponentBoard, gameSettings.minesToPlace)
}

case class PlayerStateMinePlacement(board: OwnBoard,
                                    opponentBoard: OpponentBoard,
                                    minesToPlace: Int) extends PlayerState {

  override def useMine(): ValidatedState =
    if (minesToPlace > 0)
      valid(copy(minesToPlace = minesToPlace - 1))
    else
      invalid("Cannot use a mine you do not have")

  override def withBoard(board: OwnBoard): PlayerState = copy(board = board)

  lazy val isStageOver: Boolean = minesToPlace <= 0

  // noinspection NameBooleanParameters
  override def nextStage(gameSettings: GameSettings): PlayerState =
    PlayerStateMovement(board, opponentBoard, true)
}

case class PlayerStateMovement(board: OwnBoard,
                               opponentBoard: OpponentBoard,
                               moveToMake: Boolean) extends PlayerState {

  override def moveShip(position: Position, movement: Movement): ValidatedState =
    if (moveToMake)
      ShipPlacementRule.moveShip(board, position, movement)
        .map(b => copy(board = b, moveToMake = false))
    else
      invalid("You do not have a move available")

  override def withBoard(board: OwnBoard): PlayerState = copy(board = board)

  lazy val isStageOver: Boolean = !moveToMake

  // noinspection NameBooleanParameters
  override def nextStage(gameSettings: GameSettings): PlayerState =
    PlayerStateShooting(board ,opponentBoard, true)

  override def pass(): PlayerState =
    copy(moveToMake = false)
}

case class PlayerStateShooting(board: OwnBoard,
                               opponentBoard: OpponentBoard,
                               shotToShoot: Boolean) extends PlayerState {

  override def shoot(): ValidatedState =
    if (shotToShoot)
      valid(copy(shotToShoot = false))
    else
      invalid("You do not have a shot available")

  override def withBoard(board: OwnBoard): PlayerState = copy(board = board)

  lazy val isStageOver: Boolean = !shotToShoot

  // noinspection NameBooleanParameters
  override def nextStage(gameSettings: GameSettings): PlayerState =
    PlayerStateMovement(board, opponentBoard, true)
}
