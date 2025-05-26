package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.data.OceanCell.{Ship, damagedShip, healthyShip, mine}
import io.github.teonistor.bsms.data._
import io.vavr.control.Validation
import io.vavr.control.Validation.{invalid, valid}

object ShipPlacementRule {

  def placeShip(board: OwnBoard, ship: ShipDescription, position: Position, orientation: Orientation): Validation[String, OwnBoard] = {
    val positions = (0 until ship.length)
      .map(d => position.zipWithIndex.map(p => (p, orientation) match {
        case ((coord, 0), Orientation.horizontal) => coord + d
        case ((coord, 1), Orientation.vertical) => coord + d
        case ((coord, _), _) => coord
      }))

    placeShip0(ship.name, board, positions.map((_, healthyShip)).toMap)
  }

  def moveShip(board: OwnBoard, position: Position, movement: Movement): Validation[String, OwnBoard] = {

    val hopefullyShip = board.get(position).flatMap(_.toOption)
    if (hopefullyShip.isEmpty)
      return invalid(position.mkString("You don't have a ship at (", ",", ")"))

    val Some(ShipInPlay(name, parts)) = hopefullyShip
    if (movement aligns parts.keySet)
      placeShip0(name,
        board.removedAll(parts.keys),
        parts.map { case (k,v) => (movement move k, v) })

    else
      invalid("Ship must move in the direction it is oriented")
  }

  private def placeShip0(name: String, board: OwnBoard, parts: Map[Position, Ship]): Validation[String, OwnBoard] = {
    if (parts.keys.exists(board.get(_).exists(_.isRight)))
      return invalid("Cannot place ship on top of another")

    val spawnedShip = ShipInPlay(name, parts
      .keys
      .map(pos => (pos, board.get(pos).filter(_== Left(mine)).map(_=> damagedShip).getOrElse(parts(pos))))
      .toMap)

    valid(board ++ parts.keys.map((_, Right(spawnedShip))))
  }

  def removeShip(board: OwnBoard, position: Position): OwnBoard =
    board -- board.get(position)
      .flatMap(_.toOption)
      .map(_.parts.keySet)
      .getOrElse(Set.empty)
}
