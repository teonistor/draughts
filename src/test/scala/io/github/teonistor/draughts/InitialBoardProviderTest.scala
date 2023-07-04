package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.Settings
import org.scalatest.funsuite.AnyFunSuite

class InitialBoardProviderTest extends AnyFunSuite {

  test("8x8 standard") {
    assert(new InitialBoardProvider().createBoard(
        Settings(8, 8, 2)) == Map(
      Vector[Int](0,0) -> Piece.whitePeon,
      Vector[Int](2,0) -> Piece.whitePeon,
      Vector[Int](4,0) -> Piece.whitePeon,
      Vector[Int](6,0) -> Piece.whitePeon,
      Vector[Int](1,1) -> Piece.whitePeon,
      Vector[Int](3,1) -> Piece.whitePeon,
      Vector[Int](5,1) -> Piece.whitePeon,
      Vector[Int](7,1) -> Piece.whitePeon,

      Vector[Int](0,6) -> Piece.blackPeon,
      Vector[Int](2,6) -> Piece.blackPeon,
      Vector[Int](4,6) -> Piece.blackPeon,
      Vector[Int](6,6) -> Piece.blackPeon,
      Vector[Int](1,7) -> Piece.blackPeon,
      Vector[Int](3,7) -> Piece.blackPeon,
      Vector[Int](5,7) -> Piece.blackPeon,
      Vector[Int](7,7) -> Piece.blackPeon))
  }

  test("8x8 deep") {
    assert(new InitialBoardProvider().createBoard(
        Settings(8, 8, 3)) == Map(
      Vector[Int](0,0) -> Piece.whitePeon,
      Vector[Int](2,0) -> Piece.whitePeon,
      Vector[Int](4,0) -> Piece.whitePeon,
      Vector[Int](6,0) -> Piece.whitePeon,
      Vector[Int](1,1) -> Piece.whitePeon,
      Vector[Int](3,1) -> Piece.whitePeon,
      Vector[Int](5,1) -> Piece.whitePeon,
      Vector[Int](7,1) -> Piece.whitePeon,
      Vector[Int](0,2) -> Piece.whitePeon,
      Vector[Int](2,2) -> Piece.whitePeon,
      Vector[Int](4,2) -> Piece.whitePeon,
      Vector[Int](6,2) -> Piece.whitePeon,

      Vector[Int](1,5) -> Piece.blackPeon,
      Vector[Int](3,5) -> Piece.blackPeon,
      Vector[Int](5,5) -> Piece.blackPeon,
      Vector[Int](7,5) -> Piece.blackPeon,
      Vector[Int](0,6) -> Piece.blackPeon,
      Vector[Int](2,6) -> Piece.blackPeon,
      Vector[Int](4,6) -> Piece.blackPeon,
      Vector[Int](6,6) -> Piece.blackPeon,
      Vector[Int](1,7) -> Piece.blackPeon,
      Vector[Int](3,7) -> Piece.blackPeon,
      Vector[Int](5,7) -> Piece.blackPeon,
      Vector[Int](7,7) -> Piece.blackPeon))
  }

  test("3x3") {
    assert(new InitialBoardProvider().createBoard(
        Settings(3, 3, 1)) == Map(
      Vector[Int](0,0) -> Piece.whitePeon,
      Vector[Int](2,0) -> Piece.whitePeon,
      Vector[Int](0,2) -> Piece.blackPeon,
      Vector[Int](2,2) -> Piece.blackPeon))
  }

  test("3x3x3") {
    assert(new InitialBoardProvider().createBoard(
        Settings(1, 3, 3, 3)) == Map(
      Vector[Int](0, 0, 0) -> Piece.whitePeon,
      Vector[Int](0, 2, 0) -> Piece.whitePeon,
      Vector[Int](1, 1, 0) -> Piece.whitePeon,
      Vector[Int](2, 0, 0) -> Piece.whitePeon,
      Vector[Int](2, 2, 0) -> Piece.whitePeon,
      Vector[Int](0, 0, 2) -> Piece.blackPeon,
      Vector[Int](0, 2, 2) -> Piece.blackPeon,
      Vector[Int](1, 1, 2) -> Piece.blackPeon,
      Vector[Int](2, 0, 2) -> Piece.blackPeon,
      Vector[Int](2, 2, 2) -> Piece.blackPeon))
  }
}
