package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.core.Orientation.horizontal
import io.github.teonistor.bsms.data.OceanCell.healthyShip
import io.github.teonistor.bsms.data.{ShipDescription, ShipInPlay}
import org.scalatest.funsuite.AnyFunSuiteLike

class ShipPlacementRuleTest extends AnyFunSuiteLike {

  private val fishingBoat = ShipDescription("Fishing Boat", 2)

  test("place ship") {
    val positionsWhereShipWillBe = List(
      Vector(1, 2), Vector(1, 3))
    val positionsWhereShipWontBe = List(
      Vector(0, 1), Vector(0, 2), Vector(0, 3), Vector(0, 4),
      Vector(1, 1), Vector(1, 4),
      Vector(2, 1), Vector(2, 2), Vector(2, 3), Vector(2, 4))
    val expectedShip = ShipInPlay("Fishing Boat", positionsWhereShipWillBe.map((_, healthyShip)).toMap)

    val result = ShipPlacementRule.placeShip(Map.empty, fishingBoat, Vector(1,2), horizontal)

    assert(result.isValid)
    positionsWhereShipWillBe.foreach(p => assert(result.get()(p) == Right(expectedShip)))
    positionsWhereShipWontBe.foreach(p => assert(!result.get().contains(p)))
  }
}
