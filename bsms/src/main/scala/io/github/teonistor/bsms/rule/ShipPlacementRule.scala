package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip, mine}
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

    placeShip0(ship.name, board, positions)
  }

  def moveShip(board: OwnBoard, position: Position, movement: Movement): Validation[String, OwnBoard] = {

    val hopefullyShip = board.get(position).flatMap(_.toOption)
    if (hopefullyShip.isEmpty)
      return invalid(position.mkString("You don't have a ship at (", ",", ")"))

    val Some(ShipInPlay(name, parts)) = hopefullyShip
    if (movement aligns parts.keySet)
      placeShip0(name,
        board.removedAll(parts.keys),
        parts.keySet.map(movement.move))

    else
      invalid("Ship must move in the direction it is oriented")
  }

  private def placeShip0(name: String, board: OwnBoard, positions: Iterable[Vector[Int]]): Validation[String, OwnBoard] = {
    if (positions.exists(board.get(_).exists(_.isRight)))
      return invalid("Cannot place ship on top of another")

    val spawnedShip = ShipInPlay(name, positions
      .map(pos => (pos, board.get(pos).filter(_== Left(mine)).map(_=> damagedShip).getOrElse(healthyShip)))
      .toMap)

    valid(board ++ positions.map((_, Right(spawnedShip))))
  }
}
