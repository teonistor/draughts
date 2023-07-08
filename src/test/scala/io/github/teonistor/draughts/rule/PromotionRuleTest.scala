package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.Piece
import io.github.teonistor.draughts.data.Settings
import org.scalatest.funsuite.AnyFunSuite

class PromotionRuleTest extends AnyFunSuite {

  LazyList(6,8,10,22,36).foreach(size =>
    LazyList(2,3,5,10).foreach(dimensions =>
      test(s"promote at size $size and $dimensions dimensions") {

        val input = Map(
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 2 else 0) -> Piece.blackKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 3 else 0) -> Piece.blackPeon,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 4 else 0) -> Piece.whiteKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 5 else 0) -> Piece.whitePeon,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 1 else size/2) -> Piece.blackKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 2 else size/2) -> Piece.blackPeon,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 3 else size/2) -> Piece.whiteKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 4 else size/2) -> Piece.whitePeon,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 2 else size-1) -> Piece.blackKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 3 else size-1) -> Piece.blackPeon,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 4 else size-1) -> Piece.whiteKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 5 else size-1) -> Piece.whitePeon)

        val expected = Map(
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 2 else 0) -> Piece.blackKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 3 else 0) -> Piece.blackKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 4 else 0) -> Piece.whiteKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 5 else 0) -> Piece.whitePeon,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 1 else size/2) -> Piece.blackKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 2 else size/2) -> Piece.blackPeon,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 3 else size/2) -> Piece.whiteKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 4 else size/2) -> Piece.whitePeon,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 2 else size-1) -> Piece.blackKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 3 else size-1) -> Piece.blackPeon,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 4 else size-1) -> Piece.whiteKing,
          Vector.tabulate(dimensions)(i => if (i!=dimensions-1) 5 else size-1) -> Piece.whiteKing)

        assert(new PromotionRule(Settings(1,Vector.fill(dimensions)(size):_*)).promoteAsNeeded(input) == expected)
  }))
}
