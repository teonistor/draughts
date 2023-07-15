package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{GameState, Settings}
import io.github.teonistor.draughts.rule.{AvailableMovesRule, GameOverChecker}
import org.mockito.BDDMockito.`given`
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuite
import org.springframework.test.util.ReflectionTestUtils.getField

class InitialGameProviderTest extends AnyFunSuite with IdiomaticMockito {

  test("createGame") {
    val availableMovesRule = mock[AvailableMovesRule]
    val gameOverChecker = mock[GameOverChecker]
    val initialBoardProvider = mock[InitialBoardProvider]
    val settings = mock[Settings]
    val board = mock[Map[Vector[Int], Piece]]

    given(initialBoardProvider.createBoard(settings)) willReturn board

    val result = new InitialGameProvider(availableMovesRule, gameOverChecker, initialBoardProvider).createGame(settings)
    assert(result.availableMovesRule == availableMovesRule)
    assert(getField(result.promotionRule, "settings") == settings)
    assert(result.gameOverChecker == gameOverChecker)
    assert(result.settings == settings)
    assert(result.gameState == GameState(board, Player.white, None))
  }
}
