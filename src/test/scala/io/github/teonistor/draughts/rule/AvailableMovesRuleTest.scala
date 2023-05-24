package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.data.{GameState, Position}
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
  def computeAvailableMoves(@Mock currentPlayerr: Player, @Mock myPiece: Piece, @Mock otherPiece: Piece,
                            @Mock move1: Move, @Mock move2: Move, @Mock move3: Move,
                            @Mock boardAfterMove1: Map[Position,Piece]): Unit = {
    val startingPosition = Position(3,4)
    val positionAfterMove1 = Position(5, 7)
    val positionAfterMove2 = Position(4, 4)
    val positionAfterMove3 = Position(9, 3)
    val startingBoard = Map(startingPosition -> myPiece, Position(7,8) -> otherPiece)

    given(currentPlayerr isMyPiece myPiece) willReturn true
    given(currentPlayerr isMyPiece otherPiece) willReturn false

    given(myPiece emitMoves startingPosition) willReturn Map(
      positionAfterMove1 -> move1, positionAfterMove2 -> move2, positionAfterMove3 -> move3)
    given(move1 execute startingBoard) willReturn valid(boardAfterMove1)
    given(move2 execute startingBoard) willReturn invalid("invalidity from move")

    val actual = new AvailableMovesRule().computeAvailableMoves(GameState(startingBoard, currentPlayerr))

    assert(actual == Map(
      startingPosition -> Map (
        positionAfterMove1 -> valid(boardAfterMove1),
        positionAfterMove2 -> invalid("invalidity from move"))))

    verifyNoMoreInteractions(currentPlayerr, myPiece, otherPiece, move1, move2, move3, boardAfterMove1)
  }
}
