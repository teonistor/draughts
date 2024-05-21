package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.Settings
import org.scalatest.funsuite.AnyFunSuite

class InitialBoardProviderTest extends AnyFunSuite {

  test("8x8 standard") {
    assert(new InitialBoardProvider().createBoard(
        Settings(2, 8, 8)) == Map(
      Vector(0,0) -> Piece.whitePeon,
      Vector(2,0) -> Piece.whitePeon,
      Vector(4,0) -> Piece.whitePeon,
      Vector(6,0) -> Piece.whitePeon,
      Vector(1,1) -> Piece.whitePeon,
      Vector(3,1) -> Piece.whitePeon,
      Vector(5,1) -> Piece.whitePeon,
      Vector(7,1) -> Piece.whitePeon,

      Vector(0,6) -> Piece.blackPeon,
      Vector(2,6) -> Piece.blackPeon,
      Vector(4,6) -> Piece.blackPeon,
      Vector(6,6) -> Piece.blackPeon,
      Vector(1,7) -> Piece.blackPeon,
      Vector(3,7) -> Piece.blackPeon,
      Vector(5,7) -> Piece.blackPeon,
      Vector(7,7) -> Piece.blackPeon))
  }

  test("8x8 deep") {
    assert(new InitialBoardProvider().createBoard(
        Settings(3, 8, 8)) == Map(
      Vector(0,0) -> Piece.whitePeon,
      Vector(2,0) -> Piece.whitePeon,
      Vector(4,0) -> Piece.whitePeon,
      Vector(6,0) -> Piece.whitePeon,
      Vector(1,1) -> Piece.whitePeon,
      Vector(3,1) -> Piece.whitePeon,
      Vector(5,1) -> Piece.whitePeon,
      Vector(7,1) -> Piece.whitePeon,
      Vector(0,2) -> Piece.whitePeon,
      Vector(2,2) -> Piece.whitePeon,
      Vector(4,2) -> Piece.whitePeon,
      Vector(6,2) -> Piece.whitePeon,

      Vector(1,5) -> Piece.blackPeon,
      Vector(3,5) -> Piece.blackPeon,
      Vector(5,5) -> Piece.blackPeon,
      Vector(7,5) -> Piece.blackPeon,
      Vector(0,6) -> Piece.blackPeon,
      Vector(2,6) -> Piece.blackPeon,
      Vector(4,6) -> Piece.blackPeon,
      Vector(6,6) -> Piece.blackPeon,
      Vector(1,7) -> Piece.blackPeon,
      Vector(3,7) -> Piece.blackPeon,
      Vector(5,7) -> Piece.blackPeon,
      Vector(7,7) -> Piece.blackPeon))
  }

  test("3x3") {
    assert(new InitialBoardProvider().createBoard(
        Settings(1, 3, 3)) == Map(
      Vector(0,0) -> Piece.whitePeon,
      Vector(2,0) -> Piece.whitePeon,
      Vector(0,2) -> Piece.blackPeon,
      Vector(2,2) -> Piece.blackPeon))
  }

  test("3x3x3") {
    assert(new InitialBoardProvider().createBoard(
        Settings(1, 3, 3, 3)) == Map(
      Vector(0, 0, 0) -> Piece.whitePeon,
      Vector(0, 2, 0) -> Piece.whitePeon,
      Vector(1, 1, 0) -> Piece.whitePeon,
      Vector(2, 0, 0) -> Piece.whitePeon,
      Vector(2, 2, 0) -> Piece.whitePeon,
      Vector(0, 0, 2) -> Piece.blackPeon,
      Vector(0, 2, 2) -> Piece.blackPeon,
      Vector(1, 1, 2) -> Piece.blackPeon,
      Vector(2, 0, 2) -> Piece.blackPeon,
      Vector(2, 2, 2) -> Piece.blackPeon))
  }
}
