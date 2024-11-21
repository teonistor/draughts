package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip, mine}
import io.github.teonistor.bsms.data.ShipInPlay
import org.scalatest.funsuite.AnyFunSuiteLike

class MinePlacementRuleTest extends AnyFunSuiteLike {

  test("place a mine in water") {
    val position = Vector(3, 7)

    val result = MinePlacementRule.placeMine(Map.empty, position)

    assert(result.isValid)
    assert(result.get == Map(position -> Left(mine)))

// What to do about keeping info about the opponent's board isn't so straightforward; deal with it later
//    assert(result.get._1 == Map(position -> Left(mine)))
//    assert(result.get._2 == Map(position -> miss))
  }

  test("place mine on ship") {
    val initialShip = ShipInPlay("Fishing Boat", Map(Vector(7, 2) -> healthyShip, Vector(7, 3) -> healthyShip))
    val expectedShip = ShipInPlay("Fishing Boat", Map(Vector(7, 2) -> healthyShip, Vector(7, 3) -> damagedShip))

    val result = MinePlacementRule.placeMine(
      Map(Vector(7, 2) -> Right(initialShip), Vector(7, 3) -> Right(initialShip)),
      Vector(7, 3))

    assert(result.isValid)
    assert(result.get()(Vector(7, 2)) == Right(expectedShip))
    assert(result.get()(Vector(7, 3)) == Right(expectedShip))
  }

}
