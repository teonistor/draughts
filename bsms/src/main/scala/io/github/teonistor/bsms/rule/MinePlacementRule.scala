package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip, mine}
import io.github.teonistor.bsms.data.{OwnBoard, Position}
import io.vavr.control.Validation
import io.vavr.control.Validation.valid

object MinePlacementRule {

  def placeMine(board: OwnBoard, position: Position): Validation[String, OwnBoard] =
    valid(board ++ board
      .get(position)
      .flatMap(_.toOption)
      .map(ship => Some(ship)
      .filter(_.parts(position) == healthyShip)
      .map(ship => ship.copy(parts=ship.parts + (position -> damagedShip)))
      .map(ship => ship.parts.keys.map(_-> Right(ship)))
      .getOrElse(None))
      .getOrElse(Some(position -> Left(mine))))
}
