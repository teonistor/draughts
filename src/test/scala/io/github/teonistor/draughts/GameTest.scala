package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{ComputedState, GameState, Position, Settings}
import io.github.teonistor.draughts.rule.AvailableMovesRule
import io.vavr.control.Validation
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.`given`
import org.mockito.Mock
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoSettings
import org.scalatest.Assertions

@MockitoSettings
class GameTest extends Assertions {

  @Test
  def computedState(@Mock availableMovesRule: AvailableMovesRule, @Mock settings: Settings, @Mock gameState: GameState, @Mock availableMoves: Map[Position, Map[Position, Validation[String, Map[Position, Piece]]]]): Unit = {
    val game = new Game(availableMovesRule, settings, gameState)
    given(availableMovesRule computeAvailableMoves gameState) willReturn availableMoves

    assert(game.computedState == ComputedState(availableMoves))

    verifyNoMoreInteractions(availableMovesRule, settings, gameState)
  }
}
