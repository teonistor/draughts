package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.data.Player.{alice, bob}
import org.scalatest.funsuite.AnyFunSuiteLike

class PlayerTest extends AnyFunSuiteLike {

  test("Two values") {

    assert(alice != null)
    assert(bob != null)

    assertDoesNotCompile("new Player {}")
  }

  test("other") {
    assert(alice.other == bob)
    assert(bob.other == alice)
  }

  test("toString()") {
    assert(alice.toString == "Alice")
    assert(bob.toString == "Bob")
  }
}
