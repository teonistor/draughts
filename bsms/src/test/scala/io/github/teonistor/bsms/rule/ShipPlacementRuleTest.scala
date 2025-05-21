package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.data.Movement._
import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip, mine}
import io.github.teonistor.bsms.data.Orientation.{horizontal, vertical}
import io.github.teonistor.bsms.data.{ShipDescription, ShipInPlay}
import org.scalatest.funsuite.AnyFunSuiteLike

class ShipPlacementRuleTest extends AnyFunSuiteLike {

  private val fishingBoat = ShipDescription("Fishing Boat", 2)
  private val pirateShip = ShipDescription("Pirate Ship", 4)

  test("place ship horizontally") {
    val positionsWhereShipWillBe = List(
      Vector(4, 8), Vector(5, 8))
    val positionsWhereShipWontBe = List(
      Vector(3, 7), Vector(3, 8), Vector(3, 9),
      Vector(4, 7), Vector(4, 9),
      Vector(5, 7), Vector(5, 9),
      Vector(6, 7), Vector(6, 8), Vector(6, 9))
    val expectedShip = ShipInPlay("Fishing Boat", positionsWhereShipWillBe.map((_, healthyShip)).toMap)

    val result = ShipPlacementRule.placeShip(Map.empty, fishingBoat, Vector(4,8), horizontal)

    assert(result.isValid)
    positionsWhereShipWillBe.foreach(p => assert(result.get()(p) == Right(expectedShip)))
    positionsWhereShipWontBe.foreach(p => assert(!result.get().contains(p)))
  }

  test("place ship vertically") {
    val positionsWhereShipWillBe = List(
      Vector(1, 2), Vector(1, 3))
    val positionsWhereShipWontBe = List(
      Vector(0, 1), Vector(0, 2), Vector(0, 3), Vector(0, 4),
      Vector(1, 1), Vector(1, 4),
      Vector(2, 1), Vector(2, 2), Vector(2, 3), Vector(2, 4))
    val expectedShip = ShipInPlay("Fishing Boat", positionsWhereShipWillBe.map((_, healthyShip)).toMap)

    val result = ShipPlacementRule.placeShip(Map.empty, fishingBoat, Vector(1,2), vertical)

    assert(result.isValid)
    positionsWhereShipWillBe.foreach(p => assert(result.get()(p) == Right(expectedShip)))
    positionsWhereShipWontBe.foreach(p => assert(!result.get().contains(p)))
  }

  test("place ship on mine") {
    val expectedShip = ShipInPlay("Fishing Boat", Map(Vector(5, 2) -> healthyShip, Vector(5, 3) -> damagedShip))

    val result = ShipPlacementRule.placeShip(Map(Vector(5, 3) -> Left(mine)), fishingBoat, Vector(5, 2), vertical)

    assert(result.isValid)
    assert(result.get()(Vector(5, 2)) == Right(expectedShip))
    assert(result.get()(Vector(5, 3)) == Right(expectedShip))
  }

  test("cannot place ship on ship") {
    val boardWithPirateShip = ShipPlacementRule.placeShip(Map.empty, pirateShip, Vector(3, 2), horizontal).get
    List(
        ShipPlacementRule.placeShip(boardWithPirateShip, fishingBoat, Vector(2, 2), horizontal),
        ShipPlacementRule.placeShip(boardWithPirateShip, fishingBoat, Vector(4, 2), vertical),
        ShipPlacementRule.placeShip(boardWithPirateShip, fishingBoat, Vector(6, 2), horizontal),
        ShipPlacementRule.placeShip(boardWithPirateShip, fishingBoat, Vector(5, 1), vertical))
      .foreach(result => {
        assert(result.isInvalid)
        assert(result.getError == "Cannot place ship on top of another")
      })
  }

  test("move ship horizontally") {
    val boardBeforeMovement = ShipPlacementRule.placeShip(Map.empty, pirateShip, Vector(3, 2), horizontal).get
    val boardAfterMovement = ShipPlacementRule.placeShip(Map.empty, pirateShip, Vector(4, 2), horizontal).get

    val result = ShipPlacementRule.moveShip(boardBeforeMovement, Vector(3, 2), right)
    assert(result.isValid)
    assert(result.contains(boardAfterMovement))

    assert(ShipPlacementRule.moveShip(result.get, Vector(4, 2), left).contains(boardBeforeMovement))
  }

  test("move ship vertically") {
    val boardBeforeMovement = ShipPlacementRule.placeShip(Map.empty, pirateShip, Vector(3, 2), vertical).get
    val boardAfterMovement = ShipPlacementRule.placeShip(Map.empty, pirateShip, Vector(3, 1), vertical).get

    val result = ShipPlacementRule.moveShip(boardBeforeMovement, Vector(3, 2), up)
    assert(result.isValid)
    assert(result.contains(boardAfterMovement))

    assert(ShipPlacementRule.moveShip(result.get, Vector(3, 1), down).contains(boardBeforeMovement))
  }

  test("move ship preserves damage") {
    // TODO Come here
    assert(false)
  }

  List(
      Vector(2, 0),
      Vector(1, 4),
      Vector(5, 2),
      Vector(4, 6)).foreach { position =>
    val str = position.mkString(",")

    test("cannot move ship that isn't there - " + str) {
      val result = ShipPlacementRule.moveShip(Map.empty, position, up)
      assert(result.isInvalid)
      assert(result.getError == s"You don't have a ship at ($str)")
    }
  }

  test("cannot move vertical ship horizontally") {
    val boardBeforeMovement = ShipPlacementRule.placeShip(Map.empty, pirateShip, Vector(2,4), vertical).get

    List(left, right).foreach { movement =>
      val result = ShipPlacementRule.moveShip(boardBeforeMovement, Vector(2, 4), movement)
      assert(result.isInvalid)
      assert(result.getError == "Ship must move in the direction it is oriented")
    }
  }

  test("cannot move horizontal ship vertically") {
    val boardBeforeMovement = ShipPlacementRule.placeShip(Map.empty, pirateShip, Vector(5,1), horizontal).get

    List(up, down).foreach { movement =>
      val result = ShipPlacementRule.moveShip(boardBeforeMovement, Vector(5, 1), movement)
      assert(result.isInvalid)
      assert(result.getError == "Ship must move in the direction it is oriented")
    }
  }
}
