package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.data.OceanCell.mine
import io.github.teonistor.bsms.data.{OwnBoard, Position}
import io.vavr.control.Validation
import io.vavr.control.Validation.valid

object MinePlacementRule {

  def placeMine(ownBoard: OwnBoard, position: Position): Validation[String, OwnBoard] = {
    valid(ownBoard + (position -> Left(mine)))
  }
}
