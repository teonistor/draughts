package io.github.teonistor.draughts

import io.github.teonistor.draughts.move.Move
import org.scalatest.funsuite.AnyFunSuite

class PieceTest extends AnyFunSuite {

  test("kings move where they please - all moves - 3D") {
    List(Piece.blackKing, Piece.whiteKing).foreach(piece =>
      assert(piece.emitMoves(Vector(6,3,4)) == Map(
        Vector(5,4,4) -> Move.Sliding(Vector(6,3,4), Vector(5,4,4)),
        Vector(7,2,4) -> Move.Sliding(Vector(6,3,4), Vector(7,2,4)),
        Vector(5,2,4) -> Move.Sliding(Vector(6,3,4), Vector(5,2,4)),
        Vector(7,4,4) -> Move.Sliding(Vector(6,3,4), Vector(7,4,4)),
        Vector(7,3,5) -> Move.Sliding(Vector(6,3,4), Vector(7,3,5)),
        Vector(7,3,3) -> Move.Sliding(Vector(6,3,4), Vector(7,3,3)),
        Vector(5,3,5) -> Move.Sliding(Vector(6,3,4), Vector(5,3,5)),
        Vector(5,3,3) -> Move.Sliding(Vector(6,3,4), Vector(5,3,3)),
        Vector(6,4,5) -> Move.Sliding(Vector(6,3,4), Vector(6,4,5)),
        Vector(6,4,3) -> Move.Sliding(Vector(6,3,4), Vector(6,4,3)),
        Vector(6,2,5) -> Move.Sliding(Vector(6,3,4), Vector(6,2,5)),
        Vector(6,2,3) -> Move.Sliding(Vector(6,3,4), Vector(6,2,3)),
        Vector(4,5,4) -> Move.Jumping(Vector(6,3,4), Vector(5,4,4), Vector(4,5,4)),
        Vector(8,1,4) -> Move.Jumping(Vector(6,3,4), Vector(7,2,4), Vector(8,1,4)),
        Vector(4,1,4) -> Move.Jumping(Vector(6,3,4), Vector(5,2,4), Vector(4,1,4)),
        Vector(8,5,4) -> Move.Jumping(Vector(6,3,4), Vector(7,4,4), Vector(8,5,4)),
        Vector(8,3,6) -> Move.Jumping(Vector(6,3,4), Vector(7,3,5), Vector(8,3,6)),
        Vector(8,3,2) -> Move.Jumping(Vector(6,3,4), Vector(7,3,3), Vector(8,3,2)),
        Vector(4,3,6) -> Move.Jumping(Vector(6,3,4), Vector(5,3,5), Vector(4,3,6)),
        Vector(4,3,2) -> Move.Jumping(Vector(6,3,4), Vector(5,3,3), Vector(4,3,2)),
        Vector(6,5,6) -> Move.Jumping(Vector(6,3,4), Vector(6,4,5), Vector(6,5,6)),
        Vector(6,5,2) -> Move.Jumping(Vector(6,3,4), Vector(6,4,3), Vector(6,5,2)),
        Vector(6,1,6) -> Move.Jumping(Vector(6,3,4), Vector(6,2,5), Vector(6,1,6)),
        Vector(6,1,2) -> Move.Jumping(Vector(6,3,4), Vector(6,2,3), Vector(6,1,2)))))
  }

  test("black peon moves down - all moves") {
    assert(Piece.blackPeon.emitMoves(Vector(1,7)) == Map(
      Vector(0,6) -> Move.Sliding(Vector(1,7), Vector(0,6)),
      Vector(2,6) -> Move.Sliding(Vector(1,7), Vector(2,6)),
      Vector(-1,5)-> Move.Jumping(Vector(1,7), Vector(0,6), Vector(-1,5)),
      Vector(3,5) -> Move.Jumping(Vector(1,7), Vector(2,6), Vector(3,5))))
  }

  test("white peon moves up - all moves") {
    assert(Piece.whitePeon.emitMoves(Vector(1,7)) == Map(
      Vector(0,8) -> Move.Sliding(Vector(1,7), Vector(0,8)),
      Vector(2,8) -> Move.Sliding(Vector(1,7), Vector(2,8)),
      Vector(-1,9)-> Move.Jumping(Vector(1,7), Vector(0,8), Vector(-1,9)),
      Vector(3,9) -> Move.Jumping(Vector(1,7), Vector(2,8), Vector(3,9))))
  }

  test("kings move where they please - jumps only") {
    List(Piece.blackKing, Piece.whiteKing).foreach(piece =>
      assert(piece.emitJumps(Vector(3,4)) == Map(
        Vector(5,6) -> Move.Jumping(Vector(3,4), Vector(4,5), Vector(5,6)),
        Vector(5,2) -> Move.Jumping(Vector(3,4), Vector(4,3), Vector(5,2)),
        Vector(1,6) -> Move.Jumping(Vector(3,4), Vector(2,5), Vector(1,6)),
        Vector(1,2) -> Move.Jumping(Vector(3,4), Vector(2,3), Vector(1,2)))))
  }

  test("black peon moves down - jumps only") {
    assert(Piece.blackPeon.emitJumps(Vector(1,7)) == Map(
      Vector(-1,5)-> Move.Jumping(Vector(1,7), Vector(0,6), Vector(-1,5)),
      Vector(3,5) -> Move.Jumping(Vector(1,7), Vector(2,6), Vector(3,5))))
  }

  test("white peon moves up - jumps only - 4D") {
    assert(Piece.whitePeon.emitJumps(Vector(2,0,1,7)) == Map(
      // HD Rule Choice: peons *must* move forward, otherwise they'd be like kings on any plane perpendicular to "forward"
      // HD Rule Choice: you can move in any even number of dimensions at once (and must be at least 4D to experience this, which is why I can't intuit whether that's a good idea)
      Vector(0, 0, 1,9) -> Move.Jumping(Vector(2,0,1,7), Vector(1, 0,1,8), Vector(0, 0, 1,9)),
      Vector(4, 0, 1,9) -> Move.Jumping(Vector(2,0,1,7), Vector(3, 0,1,8), Vector(4, 0, 1,9)),
      Vector(2, 2, 1,9) -> Move.Jumping(Vector(2,0,1,7), Vector(2, 1,1,8), Vector(2, 2, 1,9)),
      Vector(2,-2, 1,9) -> Move.Jumping(Vector(2,0,1,7), Vector(2,-1,1,8), Vector(2,-2, 1,9)),
      Vector(2, 0,-1,9) -> Move.Jumping(Vector(2,0,1,7), Vector(2, 0,0,8), Vector(2, 0,-1,9)),
      Vector(2, 0, 3,9) -> Move.Jumping(Vector(2,0,1,7), Vector(2, 0,2,8), Vector(2, 0, 3,9)),
      Vector(4, 2, 3,9) -> Move.Jumping(Vector(2,0,1,7), Vector(3, 1,2,8), Vector(4, 2, 3,9)),
      Vector(4, 2,-1,9) -> Move.Jumping(Vector(2,0,1,7), Vector(3, 1,0,8), Vector(4, 2,-1,9)),
      Vector(4,-2, 3,9) -> Move.Jumping(Vector(2,0,1,7), Vector(3,-1,2,8), Vector(4,-2, 3,9)),
      Vector(4,-2,-1,9) -> Move.Jumping(Vector(2,0,1,7), Vector(3,-1,0,8), Vector(4,-2,-1,9)),
      Vector(0, 2, 3,9) -> Move.Jumping(Vector(2,0,1,7), Vector(1, 1,2,8), Vector(0, 2, 3,9)),
      Vector(0, 2,-1,9) -> Move.Jumping(Vector(2,0,1,7), Vector(1, 1,0,8), Vector(0, 2,-1,9)),
      Vector(0,-2, 3,9) -> Move.Jumping(Vector(2,0,1,7), Vector(1,-1,2,8), Vector(0,-2, 3,9)),
      Vector(0,-2,-1,9) -> Move.Jumping(Vector(2,0,1,7), Vector(1,-1,0,8), Vector(0,-2,-1,9))))
  }

  test("kings promote to themselves") {
    List(Piece.blackKing, Piece.whiteKing).foreach(piece =>
      assert(piece.promote == piece))
  }

  test("black peon promotes to black king") {
    assert(Piece.blackPeon.promote == Piece.blackKing)
  }

  test("white peon promotes to white king") {
    assert(Piece.whitePeon.promote == Piece.whiteKing)
  }
}
