package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.data.OceanCell.{damagedShip, mine}
import io.github.teonistor.bsms.data.{OwnBoard, Position}
import io.vavr.control.Validation
import io.vavr.control.Validation.valid

object MinePlacementRule {

  def placeMine(board: OwnBoard, position: Position): Validation[String, OwnBoard] = {
    val u = board.get(position)
      .flatMap(_.toOption)
//      .map(ship => {
//        val updatedShip = ship.copy(parts = ship.parts + (position -> damagedShip))
//        updatedShip
//      })
      .map(ship => ship.copy(parts=ship.parts + (position -> damagedShip)))
      .map(ship => ship.parts.keys.map(_-> Right(ship)))
      .getOrElse(Some(position -> Left(mine)))

    valid(board ++ u)
  }
}
