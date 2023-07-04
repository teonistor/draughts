package io.github.teonistor.draughts.data

import org.scalatest.funsuite.AnyFunSuite

class SettingsTest extends AnyFunSuite {

  test("you must have at least 2 dimensions") {
    assertThrows[IllegalArgumentException](Settings(1, 4))
    assertThrows[IllegalArgumentException](Settings(0))
  }

  test("all dimensions must be greater than 1") {
    assertThrows[IllegalArgumentException](Settings(1, 1, 2, -1, 3))
  }

  test("you must have at least 1 starting row") {
    assertThrows[IllegalArgumentException](Settings(0, 2, 2, 2))
  }

  test("startingRows must be less than half of boardHeight") {
    assertThrows[IllegalArgumentException](Settings(2, 6, 5, 4))
    assertThrows[IllegalArgumentException](Settings(4, 9, 8, 7))

    Settings(1, 6, 5, 4)
    Settings(3, 9, 8, 7)
  }

  test("startingRows must be less than half of boardHeight - legacy") {
    assertThrows[IllegalArgumentException](Settings(4, 4, 2))
    assertThrows[IllegalArgumentException](Settings(7, 7, 4))

    Settings(4, 4, 1)
    Settings(7, 7, 3)
  }
}
