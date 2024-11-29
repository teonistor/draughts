package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.core.{BattleshipMinesweeper, PlayerState}
import io.github.teonistor.bsms.data.GameSettings
import org.mockito.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike

class StageChangeTest extends AnyFunSuiteLike with IdiomaticMockito {

  private val settings = mock[GameSettings]
  private val aliceState = mock[PlayerState]
  private val bobState = mock[PlayerState]

  test("change stage when all states ended the current stage") {
    val aliceStateAfter = mock[PlayerState]
    val bobStateAfter = mock[PlayerState]
    aliceState.isStageOver returns true
    bobState.isStageOver returns true
    aliceState.nextStage(settings) returns aliceStateAfter
    bobState.nextStage(settings) returns bobStateAfter

    val result = StageChange.advanceStageIfNecessary(BattleshipMinesweeper(settings, aliceState, bobState))
    assert(result == BattleshipMinesweeper(settings, aliceStateAfter, bobStateAfter))
  }

  test("don't change stage otherwise - 1") {
    aliceState.isStageOver returns false
    bobState.isStageOver returns true

    val game = BattleshipMinesweeper(settings, aliceState, bobState)
    val result = StageChange.advanceStageIfNecessary(game)

    assert(result eq game)
  }

  test("don't change stage otherwise - 2") {
    aliceState.isStageOver returns true
    bobState.isStageOver returns false

    val game = BattleshipMinesweeper(settings, aliceState, bobState)
    val result = StageChange.advanceStageIfNecessary(game)

    assert(result eq game)
  }
}
