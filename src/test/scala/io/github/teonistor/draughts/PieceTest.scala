package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.Position
import io.github.teonistor.draughts.move.Move
import org.scalatest.funsuite.AnyFunSuite

class PieceTest extends AnyFunSuite {

  test("kings move where they please") {
    List(Piece.blackKing, Piece.whiteKing).foreach(piece =>
      assert(piece.emitMoves(Position(3,4)) == Map(
        Position(4,5) -> Move.Sliding(Position(3,4), Position(4,5)),
        Position(4,3) -> Move.Sliding(Position(3,4), Position(4,3)),
        Position(2,5) -> Move.Sliding(Position(3,4), Position(2,5)),
        Position(2,3) -> Move.Sliding(Position(3,4), Position(2,3)),
        Position(5,6) -> Move.Jumping(Position(3,4), Position(4,5), Position(5,6)),
        Position(5,2) -> Move.Jumping(Position(3,4), Position(4,3), Position(5,2)),
        Position(1,6) -> Move.Jumping(Position(3,4), Position(2,5), Position(1,6)),
        Position(1,2) -> Move.Jumping(Position(3,4), Position(2,3), Position(1,2)))))
  }

  test("black peon moves down") {
    assert(Piece.blackPeon.emitMoves(Position(1,7)) == Map(
      Position(0,6) -> Move.Sliding(Position(1,7), Position(0,6)),
      Position(2,6) -> Move.Sliding(Position(1,7), Position(2,6)),
      Position(-1,5)-> Move.Jumping(Position(1,7), Position(0,6), Position(-1,5)),
      Position(3,5) -> Move.Jumping(Position(1,7), Position(2,6), Position(3,5))))
  }

  test("white peon moves up") {
    assert(Piece.whitePeon.emitMoves(Position(1,7)) == Map(
      Position(0,8) -> Move.Sliding(Position(1,7), Position(0,8)),
      Position(2,8) -> Move.Sliding(Position(1,7), Position(2,8)),
      Position(-1,9)-> Move.Jumping(Position(1,7), Position(0,8), Position(-1,9)),
      Position(3,9) -> Move.Jumping(Position(1,7), Position(2,8), Position(3,9))))
  }

  test("kings promote to themselves") {
    List(Piece.blackKing, Piece.whiteKing).foreach(piece =>
      assert(piece.promote == piece))
  }

  test("black peon promotea to black king") {
    assert(Piece.blackPeon.promote == Piece.blackKing)
  }

  test("white peon promotea to white king") {
    assert(Piece.whitePeon.promote == Piece.whiteKing)
  }
}
