package io.github.teonistor.draughts

import org.scalatest.funsuite.AnyFunSuite

class PlayerTest extends AnyFunSuite {

  test("next() reciprocity") {
    assert(Player.black.next == Player.white)
    assert(Player.white.next == Player.black)
  }

  test("black pieces are black's") {
    assert(Player.black isMyPiece Piece.blackKing)
    assert(Player.black isMyPiece Piece.blackPeon)
  }

  test("white pieces are white's") {
    assert(Player.white isMyPiece Piece.whiteKing)
    assert(Player.white isMyPiece Piece.whitePeon)
  }

  test("black pieces are not white's") {
    assert(!Player.white.isMyPiece(Piece.blackKing))
    assert(!Player.white.isMyPiece(Piece.blackPeon))
  }

  test("white pieces are not black's") {
    assert(!Player.black.isMyPiece(Piece.whiteKing))
    assert(!Player.black.isMyPiece(Piece.whitePeon))
  }
}
