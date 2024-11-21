package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.core.Orientation
import io.github.teonistor.bsms.data.OceanCell.healthyShip
import io.github.teonistor.bsms.data.{OwnBoard, Position, ShipDescription, ShipInPlay}
import io.vavr.control.Validation
import io.vavr.control.Validation.valid

object ShipPlacementRule {

  def placeShip(board: OwnBoard, ship: ShipDescription, position: Position, orientation: Orientation): Validation[String, OwnBoard] = {
     val tp = (0 until ship.length)
       .map(d => position.zipWithIndex.map {
         case (coord, i) => if (i == 1) coord + d else coord
       })

      val tb = ShipInPlay(ship.name, tp.map((_, healthyShip)).toMap)

      valid(board ++ tp.map((_, Right(tb))))
  }
}
