package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.data.OceanCell.{Ship, damagedShip}

case class ShipInPlay(name: String,
                      parts: Map[Position, Ship]) {

  lazy val isCompletelyDamaged: Boolean = parts.values.forall(_== damagedShip)
}
