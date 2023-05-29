package io.github.teonistor.draughts.data

import org.scalatest.funsuite.AnyFunSuite

class SettingsTest extends AnyFunSuite {

  test("startingRows must be less than half of boardHeight") {
    assertThrows[IllegalArgumentException](Settings(4, 4, 2))
    assertThrows[IllegalArgumentException](Settings(7, 7, 4))

    Settings(4, 4, 1)
    Settings(7, 7, 3)
  }
}
