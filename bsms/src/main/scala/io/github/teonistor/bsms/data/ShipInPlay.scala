package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.data.OceanCell.damagedShip

case class ShipInPlay(name: String,
                      parts: Map[Position, ShipCell]) {

  lazy val isCompletelyDamaged: Boolean = parts.values.forall(_== damagedShip)
}
