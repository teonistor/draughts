package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{Position, Settings}
import org.scalatest.funsuite.AnyFunSuite

class InitialBoardProviderTest extends AnyFunSuite {

  test("8x8 standard") {
    assert(new InitialBoardProvider().createBoard(
        Settings(8, 8, 2)) == Map(
      Position(0,0) -> Piece.whitePeon,
      Position(2,0) -> Piece.whitePeon,
      Position(4,0) -> Piece.whitePeon,
      Position(6,0) -> Piece.whitePeon,
      Position(1,1) -> Piece.whitePeon,
      Position(3,1) -> Piece.whitePeon,
      Position(5,1) -> Piece.whitePeon,
      Position(7,1) -> Piece.whitePeon,

      Position(0,6) -> Piece.blackPeon,
      Position(2,6) -> Piece.blackPeon,
      Position(4,6) -> Piece.blackPeon,
      Position(6,6) -> Piece.blackPeon,
      Position(1,7) -> Piece.blackPeon,
      Position(3,7) -> Piece.blackPeon,
      Position(5,7) -> Piece.blackPeon,
      Position(7,7) -> Piece.blackPeon))
  }

  test("8x8 deep") {
    assert(new InitialBoardProvider().createBoard(
        Settings(8, 8, 3)) == Map(
      Position(0,0) -> Piece.whitePeon,
      Position(2,0) -> Piece.whitePeon,
      Position(4,0) -> Piece.whitePeon,
      Position(6,0) -> Piece.whitePeon,
      Position(1,1) -> Piece.whitePeon,
      Position(3,1) -> Piece.whitePeon,
      Position(5,1) -> Piece.whitePeon,
      Position(7,1) -> Piece.whitePeon,
      Position(0,2) -> Piece.whitePeon,
      Position(2,2) -> Piece.whitePeon,
      Position(4,2) -> Piece.whitePeon,
      Position(6,2) -> Piece.whitePeon,

      Position(1,5) -> Piece.blackPeon,
      Position(3,5) -> Piece.blackPeon,
      Position(5,5) -> Piece.blackPeon,
      Position(7,5) -> Piece.blackPeon,
      Position(0,6) -> Piece.blackPeon,
      Position(2,6) -> Piece.blackPeon,
      Position(4,6) -> Piece.blackPeon,
      Position(6,6) -> Piece.blackPeon,
      Position(1,7) -> Piece.blackPeon,
      Position(3,7) -> Piece.blackPeon,
      Position(5,7) -> Piece.blackPeon,
      Position(7,7) -> Piece.blackPeon))
  }

  test("tiny") {
    assert(new InitialBoardProvider().createBoard(
        Settings(3, 3, 1)) == Map(
      Position(0,0) -> Piece.whitePeon,
      Position(2,0) -> Piece.whitePeon,
      Position(0,2) -> Piece.blackPeon,
      Position(2,2) -> Piece.blackPeon))
  }

//  test("massive") {
//    assert(new InitialBoardProvider().createBoard(
//      Settings(3, 3, 1)) == Map(
//      Position(0, 0) -> Piece.whitePeon,
//      Position(2, 0) -> Piece.whitePeon,
//      Position(0, 2) -> Piece.blackPeon,
//      Position(2, 2) -> Piece.blackPeon))
//  }
}
