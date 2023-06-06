package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.Piece
import io.github.teonistor.draughts.data.{Position, Settings}
import org.scalatest.funsuite.AnyFunSuite

class PromotionRuleTest extends AnyFunSuite {

  LazyList(6,8,10,22,36).foreach(size => test(s"promote at size $size") {
    val input = Map(
      Position(2, 0) -> Piece.blackKing,
      Position(3, 0) -> Piece.blackPeon,
      Position(4, 0) -> Piece.whiteKing,
      Position(5, 0) -> Piece.whitePeon,
      Position(1, size/2) -> Piece.blackKing,
      Position(2, size/2) -> Piece.blackPeon,
      Position(3, size/2) -> Piece.whiteKing,
      Position(4, size/2) -> Piece.whitePeon,
      Position(2, size-1) -> Piece.blackKing,
      Position(3, size-1) -> Piece.blackPeon,
      Position(4, size-1) -> Piece.whiteKing,
      Position(5, size-1) -> Piece.whitePeon)

    val expected = Map(
      Position(2, 0) -> Piece.blackKing,
      Position(3, 0) -> Piece.blackKing,
      Position(4, 0) -> Piece.whiteKing,
      Position(5, 0) -> Piece.whitePeon,
      Position(1, size/2) -> Piece.blackKing,
      Position(2, size/2) -> Piece.blackPeon,
      Position(3, size/2) -> Piece.whiteKing,
      Position(4, size/2) -> Piece.whitePeon,
      Position(2, size-1) -> Piece.blackKing,
      Position(3, size-1) -> Piece.blackPeon,
      Position(4, size-1) -> Piece.whiteKing,
      Position(5, size-1) -> Piece.whiteKing)

    assert(new PromotionRule(Settings(size,size,1)).promoteAsNeeded(input) == expected)
  })
}
