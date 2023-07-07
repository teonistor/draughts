package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.data.{GameState, Settings}
import io.github.teonistor.draughts.move.Move
import io.github.teonistor.draughts.{Piece, Player}
import io.vavr.control.Validation.{invalid, valid}
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.`given`
import org.mockito.Mock
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoSettings
import org.scalatest.Assertions

@MockitoSettings
class AvailableMovesRuleTest extends Assertions {

  @Test
  def computeAvailableMovesNormally(@Mock currentPlayer: Player, @Mock myPiece: Piece, @Mock otherPiece: Piece,
                                    @Mock move1: Move, @Mock move2: Move, @Mock move3: Move,
                                    @Mock stateAfterMove1: GameState): Unit = {
    val startingPosition = Vector(3, 4)
    val positionAfterMove1 = Vector(7, 9)
    val positionAfterMove2 = Vector(4, 4)
    val positionOutsideBoard1 = Vector(8, 3)
    val positionOutsideBoard2 = Vector(2, 10)
    val positionOutsideBoard3 = Vector(1, -1)
    val positionOutsideBoard4 = Vector(-1, 6)
    val startingState = GameState(Map(startingPosition -> myPiece, Vector(7, 8) -> otherPiece), currentPlayer, None)

    given(currentPlayer isMyPiece myPiece) willReturn true
    given(currentPlayer isMyPiece otherPiece) willReturn false

    given(myPiece emitMoves startingPosition) willReturn Map(
      positionAfterMove1 -> move1, positionAfterMove2 -> move2,
      positionOutsideBoard1 -> move3,
      positionOutsideBoard2 -> move3,
      positionOutsideBoard3 -> move3,
      positionOutsideBoard4 -> move3)
    given(move1 execute startingState) willReturn valid(stateAfterMove1)
    given(move2 execute startingState) willReturn invalid("invalidity from move")

    val actual = new AvailableMovesRule().computeAvailableMoves(startingState, Settings(8, 10, 3))

    assert(actual == Map(
      startingPosition -> Map (
        positionAfterMove1 -> valid(stateAfterMove1),
        positionAfterMove2 -> invalid("invalidity from move"))))

    verifyNoMoreInteractions(currentPlayer, myPiece, otherPiece, move1, move2, move3, stateAfterMove1)
  }

  @Test
  def computeAvailableMovesWhenContinuingJump(@Mock currentPlayer: Player, @Mock piece1: Piece, @Mock piece2: Piece,
                                              @Mock move1: Move, @Mock move2: Move,
                                              @Mock stateAfterMove1: GameState): Unit = {
    val startingPosition = Vector(3, 4)
    val otherStartingPosition = Vector(5, 4)
    val positionAfterMove1 = Vector(7, 9)
    val positionAfterMove2 = Vector(4, 4)
    val startingState = GameState(Map(startingPosition -> piece1, otherStartingPosition -> piece2), currentPlayer, Some(startingPosition))

    given(currentPlayer isMyPiece piece1) willReturn true
    given(piece1.emitJumps(startingPosition)) willReturn Map(positionAfterMove1 -> move1, positionAfterMove2 -> move2)
    given(move1 execute startingState) willReturn valid(stateAfterMove1)
    given(move2 execute startingState) willReturn invalid("invalidity from move")

    val actual = new AvailableMovesRule().computeAvailableMoves(startingState, Settings(8, 10, 3))

    assert(actual == Map(
      startingPosition -> Map(
        positionAfterMove1 -> valid(stateAfterMove1),
        positionAfterMove2 -> invalid("invalidity from move"))))

    verifyNoMoreInteractions(currentPlayer, piece1, piece2, move1, move2, stateAfterMove1)
  }
}
