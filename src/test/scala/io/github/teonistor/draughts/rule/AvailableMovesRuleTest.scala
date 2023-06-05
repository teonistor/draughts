package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.data.{GameState, Position, Settings}
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
    val positionAfterMove1 = Position(7, 9)
    val positionAfterMove2 = Position(4, 4)
    val positionOutsideBoard1 = Position(8, 3)
    val positionOutsideBoard2 = Position(2, 10)
    val positionOutsideBoard3 = Position(1, -1)
    val positionOutsideBoard4 = Position(-1, 6)
    val startingBoard = Map(startingPosition -> myPiece, Position(7,8) -> otherPiece)

    given(currentPlayerr isMyPiece myPiece) willReturn true
    given(currentPlayerr isMyPiece otherPiece) willReturn false

    given(myPiece emitMoves startingPosition) willReturn Map(
      positionAfterMove1 -> move1, positionAfterMove2 -> move2,
      positionOutsideBoard1 -> move3,
      positionOutsideBoard2 -> move3,
      positionOutsideBoard3 -> move3,
      positionOutsideBoard4 -> move3)
    given(move1 execute startingBoard) willReturn valid(boardAfterMove1)
    given(move2 execute startingBoard) willReturn invalid("invalidity from move")

    val actual = new AvailableMovesRule().computeAvailableMoves(GameState(startingBoard, currentPlayerr, None), Settings(8, 10, 3))

    assert(actual == Map(
      startingPosition -> Map (
        positionAfterMove1 -> valid(boardAfterMove1),
        positionAfterMove2 -> invalid("invalidity from move"))))

    verifyNoMoreInteractions(currentPlayerr, myPiece, otherPiece, move1, move2, move3, boardAfterMove1)
  }
}
