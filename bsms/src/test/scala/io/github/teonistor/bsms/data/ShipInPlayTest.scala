package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip}
import org.mockito.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike

class ShipInPlayTest extends AnyFunSuiteLike with IdiomaticMockito {

  private val (p, q) = (mock[Position], mock[Position])

  test("not damaged") {
    assert(!ShipInPlay(Map(p -> healthyShip, q -> healthyShip)).isCompletelyDamaged)
  }

  test("a bit damaged but not completely") {
    assert(!ShipInPlay(Map(p -> healthyShip, q -> damagedShip)).isCompletelyDamaged)
  }

  test("completely damaged") {
    assert(ShipInPlay(Map(p -> damagedShip, q -> damagedShip)).isCompletelyDamaged)
  }
}
