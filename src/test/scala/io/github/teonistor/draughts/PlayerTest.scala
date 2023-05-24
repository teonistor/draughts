package io.github.teonistor.draughts

import org.scalatest.funsuite.AnyFunSuite

class PlayerTest extends AnyFunSuite {

  test("next() reciprocity") {
    assert(Player.black.next == Player.white)
    assert(Player.white.next == Player.black)
  }
}
