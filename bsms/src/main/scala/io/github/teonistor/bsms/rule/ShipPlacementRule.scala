package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.core.Orientation
import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip, mine}
import io.github.teonistor.bsms.data.{OwnBoard, Position, ShipDescription, ShipInPlay}
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

    placeShip0(board, ship.name, positions)
  }

  def moveShip(board: OwnBoard, position: Position, movement: Vector[Int]): Validation[String, OwnBoard] = {
    val Right(ShipInPlay(name, parts)) = board(position)
    val lifted = board.removedAll(parts.keys)
    val poss = parts.keySet.map(_.lazyZip(movement).map(_+_))

    placeShip0(lifted, name, poss)
  }

  private def placeShip0(board: OwnBoard, name: String, positions: Iterable[Vector[Int]]): Validation[String, OwnBoard] = {
    if (positions.exists(board.get(_).exists(_.isRight)))
      return invalid("Cannot place ship on top of another")

    val spawnedShip = ShipInPlay(name, positions
      .map(pos => (pos, board.get(pos).filter(_== Left(mine)).map(_=> damagedShip).getOrElse(healthyShip)))
      .toMap)

    valid(board ++ positions.map((_, Right(spawnedShip))))
  }
}
