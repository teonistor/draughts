package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{GameState, Settings}
import io.github.teonistor.draughts.rule.{AvailableMoves, AvailableMovesRule, GameOverChecker, PromotionRule}
import io.vavr.control.Validation.{invalid, valid}
import org.junit.jupiter.api.{BeforeEach, Nested, Test}
import org.mockito.BDDMockito.`given`
import org.mockito.Mock
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoSettings
import org.scalatest.Assertions

import scala.util.Random.nextBoolean

@MockitoSettings
class GameTest extends Assertions {

  @Test
  def computedState(@Mock availableMovesRule: AvailableMovesRule, @Mock promotionRule: PromotionRule, @Mock gameOverChecker: GameOverChecker, @Mock settings: Settings, @Mock gameState: GameState, @Mock availableMoves: AvailableMoves): Unit = {
    val game = new Game(availableMovesRule, promotionRule, gameOverChecker, settings, gameState)

    val isGameOver = nextBoolean()
    given(availableMovesRule.computeAvailableMoves(gameState, settings)) willReturn availableMoves
    given(gameOverChecker.isGameOver(gameState, availableMoves)) willReturn isGameOver

    assert(game.availableMoves == availableMoves)
    assert(game.isGameOver == isGameOver)

    verifyNoMoreInteractions(availableMovesRule, settings, gameState)
  }

  @Nested
  class Move {
    private val nil  = Vector(1,2)
    private val from = Vector(2,3)
    private val good = Vector(3,4)
    private val bad  = Vector(4,5)

    @Mock private var promotionRule: PromotionRule =_
    @Mock private var boardAfterMove: Map[Vector[Int],Piece] =_
    @Mock private var boardAfterPromotion: Map[Vector[Int],Piece] =_
    private var game: Game =_

    @BeforeEach
    def before(@Mock availableMovesRule: AvailableMovesRule, @Mock gameOverChecker: GameOverChecker, @Mock settings: Settings): Unit = {
      val gameState = GameState(null, Player.black, None)
      this.game = new Game(availableMovesRule, promotionRule, gameOverChecker, settings, gameState)

      given(availableMovesRule.computeAvailableMoves(gameState, settings)) willReturn Map(
        from -> Map(
          good -> valid(GameState(boardAfterMove, Player.white, None)),
          bad -> invalid("invalidity from computation")))
    }

    @Test
    def cannotMoveFromWhereYouDontHaveAPiece(): Unit = {
      assert(game.move(nil, good).getError == "You don't have a piece at (1,2)")
    }

    @Test
    def cannotMoveToWhereYouCantReach(): Unit = {
      assert(game.move(from, nil).getError == "Your piece from (2,3) cannot reach (1,2)")
    }

    @Test
    def cannotMoveWhereTheMoveIsInvalid(): Unit = {
      assert(game.move(from, bad).getError == "invalidity from computation")
    }

    @Test
    def moveSuccessfully(): Unit = {
      given(promotionRule promoteAsNeeded boardAfterMove) willReturn boardAfterPromotion
      val newState = game.move(from, good).get.gameState
      assert(newState.board == boardAfterPromotion)
      assert(newState.currentPlayer == Player.white)
    }
  }

  @Nested
  class PassJump {

    @Test
    def yes(): Unit = {
      val in = new Game(null, null, null, null, GameState(null, Player.black, Some(null)))
      val actual:Game = in.pass().get

      assert(actual.gameState.currentPlayer == Player.white)
      assert(actual.gameState.ongoingJump.isEmpty)
    }

    @Test
    def no(): Unit = {
      val in = new Game(null, null, null, null, GameState(null, Player.black, None))

      assert(in.pass().getError == "No jump in progress")
    }
  }
}
