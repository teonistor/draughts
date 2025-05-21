package io.github.teonistor.bsms.data

sealed trait OceanCell

object OceanCell {

  sealed trait Occupied extends OceanCell

  sealed trait Ship extends Occupied

  val water: OceanCell = new OceanCell {}
  val mine: Occupied = new Occupied {}
  val healthyShip: Ship = new Ship {}
  val damagedShip: Ship = new Ship {}
}
