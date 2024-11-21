package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.core.Orientation
import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip, mine}
import io.github.teonistor.bsms.data.{OwnBoard, Position, ShipDescription, ShipInPlay}
import io.vavr.control.Validation
import io.vavr.control.Validation.valid

object ShipPlacementRule {

  def placeShip(board: OwnBoard, ship: ShipDescription, position: Position, orientation: Orientation): Validation[String, OwnBoard] = {
    val positions = (0 until ship.length)
      .map(d => position.zipWithIndex.map(p => (p, orientation) match {
        case ((coord, 0), Orientation.horizontal) => coord + d
        case ((coord, 1), Orientation.vertical) => coord + d
        case ((coord, _), _) => coord
      }))

    val spawnedShip = ShipInPlay(ship.name, positions
      .map(pos => (pos, board.get(pos).filter(_== Left(mine)).map(_=> damagedShip).getOrElse(healthyShip)))
      .toMap)

    valid(board ++ positions.map((_, Right(spawnedShip))))
  }
}
