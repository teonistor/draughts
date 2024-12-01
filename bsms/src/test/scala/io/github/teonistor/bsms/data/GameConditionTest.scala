package io.github.teonistor.bsms.data

import org.scalatest.funsuite.AnyFunSuite

class GameConditionTest extends AnyFunSuite {

  test("Five values") {

    assert(GameCondition.continues != null)
    assert(GameCondition.aliceWins != null)
    assert(GameCondition.bobWins != null)
    assert(GameCondition.everyoneLoses != null)
    assert(GameCondition.stalemate != null)

    assertDoesNotCompile("new GameCondition {}")
  }
}
