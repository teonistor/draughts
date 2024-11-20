package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.data.{OceanCell, PlayerState, ShipDescription}
import org.scalatest.funsuite.AnyFunSuiteLike

class BattleshipMinesweeperTest extends AnyFunSuiteLike {

  test("place a ship") {
    val positionsWhereShipWillBeLater = List(
      Vector(1, 1), Vector(1, 2))
    val positionsWhereShipWontBe = List(
      Vector(0, 0), Vector(0, 1), Vector(0, 2), Vector(0, 3),
      Vector(1, 0), Vector(1, 3),
      Vector(2, 0), Vector(2, 1), Vector(2, 2), Vector(2, 3))

    val game = new BattleshipMinesweeper(PlayerState(Map.empty, Map.empty, Set.empty, 0))
    (positionsWhereShipWillBeLater ++ positionsWhereShipWontBe)
      .foreach(p => assert(game.inspect(1, p) == OceanCell.water))

    val result = game.placeShip(1, Vector(1, 1), ShipDescription("Paper boat", 2), true)
    assert(result.isValid)
    positionsWhereShipWillBeLater.foreach(p => assert(result.get.inspect(1, p) == OceanCell.healthyShip))
    positionsWhereShipWontBe.foreach(p => assert(result.get.inspect(1, p) == OceanCell.water))
  }
}
