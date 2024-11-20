package io.github.teonistor.bsms.data

sealed trait OceanCell

sealed trait ShipCell extends OceanCell

object OceanCell {

  val water: OceanCell = new OceanCell {}
  val mine: OceanCell = new OceanCell {}
  val healthyShip: ShipCell = new ShipCell {}
  val damagedShip: ShipCell = new ShipCell {}
}
