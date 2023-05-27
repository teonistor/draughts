package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.Piece
import io.github.teonistor.draughts.data.Position
import org.scalatest.funsuite.AnyFunSuite

class PromotionRuleTest extends AnyFunSuite {

  test("promote as needed"){
    val input = Map(
        Position(2,0) -> Piece.blackKing,
        Position(1,1) -> Piece.blackPeon,
        Position(2,0) -> Piece.blackPeon,
        Position(4,0) -> Piece.whitePeon,

        Position(3,7) -> Piece.whiteKing,
        Position(5,7) -> Piece.whitePeon,
        Position(6,6) -> Piece.whitePeon,
        Position(7,7) -> Piece.blackPeon)

    val expected = Map(
        Position(2,0) -> Piece.blackKing,
        Position(1,1) -> Piece.blackPeon,
        Position(2,0) -> Piece.blackKing,
        Position(4,0) -> Piece.whitePeon,

        Position(3,7) -> Piece.whiteKing,
        Position(5,7) -> Piece.whiteKing,
        Position(6,6) -> Piece.whitePeon,
        Position(7,7) -> Piece.blackPeon)

    assert(new PromotionRule().promoteAsNeeded(input) == expected)
  }
}
