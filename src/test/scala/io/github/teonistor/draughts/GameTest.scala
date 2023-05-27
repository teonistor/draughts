package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{ComputedState, GameState, Position, Settings}
import io.github.teonistor.draughts.rule.{AvailableMoves, AvailableMovesRule, PromotionRule}
import io.vavr.control.Validation.{invalid, valid}
import org.junit.jupiter.api.{BeforeEach, Nested, Test}
import org.mockito.BDDMockito.`given`
import org.mockito.Mock
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoSettings
import org.scalatest.Assertions

@MockitoSettings
class GameTest extends Assertions {

  @Test
  def computedState(@Mock availableMovesRule: AvailableMovesRule, @Mock promotionRule: PromotionRule, @Mock settings: Settings, @Mock gameState: GameState, @Mock availableMoves: AvailableMoves): Unit = {
    val game = new Game(availableMovesRule, promotionRule, settings, gameState)
    given(availableMovesRule computeAvailableMoves gameState) willReturn availableMoves

    assert(game.computedState == ComputedState(availableMoves))

    verifyNoMoreInteractions(availableMovesRule, settings, gameState)
  }

  @Nested
  class Move {
    private val nil  = Position(1,2)
    private val from = Position(2,3)
    private val good = Position(3,4)
    private val bad  = Position(4,5)

    @Mock private var promotionRule: PromotionRule =_
    @Mock private var boardAfterMove: Map[Position,Piece] =_
    @Mock private var boardAfterPromotion: Map[Position,Piece] =_
    private var game: Game =_

    @BeforeEach
    def before(@Mock availableMovesRule: AvailableMovesRule, @Mock settings: Settings): Unit = {
      val gameState = GameState(null, Player.black)
      this.game = new Game(availableMovesRule, promotionRule, settings, gameState)

      given(availableMovesRule computeAvailableMoves gameState) willReturn Map(
        from -> Map(
          good -> valid(boardAfterMove),
          bad -> invalid("invalidity from computation")))
    }

    @Test
    def cannotMoveFromWhereYouDontHaveAPiece(): Unit = {
      assert(game.move(nil, good).getError == "You don't have a piece at Position(1,2)")
    }

    @Test
    def cannotMoveToWhereYouCantReach(): Unit = {
      assert(game.move(from, nil).getError == "Your piece from Position(2,3) cannot reach Position(1,2)")
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


}
