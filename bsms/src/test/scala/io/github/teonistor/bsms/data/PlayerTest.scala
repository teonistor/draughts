package io.github.teonistor.bsms.data

import org.scalatest.funsuite.AnyFunSuiteLike

class PlayerTest extends AnyFunSuiteLike {

  test("Two values") {

    assert(Player.alice != null)
    assert(Player.bob != null)

    assertDoesNotCompile("new Player {}")
  }

  test("toString()") {
    assert(Player.alice.toString == "Alice")
    assert(Player.bob.toString == "Bob")
  }
}
