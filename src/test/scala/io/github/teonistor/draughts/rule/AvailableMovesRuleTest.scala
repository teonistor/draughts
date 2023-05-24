package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.move.Move
import io.github.teonistor.draughts.{Piece, Position, State}
import io.vavr.control.Validation.{invalid, valid}
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.`given`
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoSettings
import org.scalatest.Assertions

@MockitoSettings
class AvailableMovesRuleTest extends Assertions {

  @Test
  def computeAvailableMoves(@Mock myPiece: Piece, @Mock otherPiece: Piece,
                            @Mock move1: Move, @Mock move2: Move, @Mock move3: Move,
                            @Mock boardAfterMove1: Map[Position,Piece]): Unit = {
    val startingPosition = Position(3,4)
    val positionAfterMove1 = Position(5, 7)
    val positionAfterMove2 = Position(4, 4)
    val positionAfterMove3 = Position(9, 3)
    val startingBoard = Map(startingPosition -> myPiece, Position(7,8) -> otherPiece)

    // This will be removed later
    given(otherPiece.emitMoves(Position(7,8))).willReturn(Map.empty)

    given(myPiece.emitMoves(startingPosition)).willReturn(Map(
      positionAfterMove1 -> move1, positionAfterMove2 -> move2, positionAfterMove3 -> move3))
    given(move1.execute(startingBoard)).willReturn(valid(boardAfterMove1))
    given(move2.execute(startingBoard)).willReturn(invalid("invalidity from move"))

    val actual = new AvailableMovesRule().computeAvailableMoves(new State{
      def board = startingBoard
    })

    assert(actual == Map(
      startingPosition -> Map (
        positionAfterMove1 -> valid(boardAfterMove1),
        positionAfterMove2 -> invalid("invalidity from move"))))
  }
}
