package io.github.teonistor.bsms.data

import org.scalatest.funsuite.AnyFunSuite

class OceanCellTest extends AnyFunSuite {

  test("Four values") {

    assert(OceanCell.water != null)
    assert(OceanCell.mine != null)
    assert(OceanCell.healthyShip != null)
    assert(OceanCell.damagedShip != null)

    assertDoesNotCompile("new OceanCell {}")
    assertDoesNotCompile("new ShipCell {}")
  }
}
