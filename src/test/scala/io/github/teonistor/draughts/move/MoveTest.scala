package io.github.teonistor.draughts.move

import io.github.teonistor.draughts.Piece
import io.github.teonistor.draughts.data.Position
import org.scalatest.funsuite.AnyFunSuite

class MoveTest extends AnyFunSuite {

  // TODO Generalise
  private val moveMe = Piece.whitePeon
  private val ignoreMe = Piece.whitePeon
  private val captureMe = Piece.blackPeon
  private val iBlockU = Piece.whitePeon

  test("sliding move happens") {
    assert(Move.Sliding(Position(2,2), Position(3,3))
      .execute(Map(Position(2,2) -> moveMe, Position(7,7) -> ignoreMe))
      .get ==
      Map(Position(3,3) -> moveMe, Position(7,7) -> ignoreMe))
  }

  test("sliding move refuses because destination is occupied") {
    assert(Move.Sliding(Position(6,6), Position(5,5))
      .execute(Map(Position(6,6) -> moveMe, Position(5,5) -> iBlockU))
      .getError ==
      "Position(5,5) is occupied")
  }

  test("jumping move happens") {
    assert(Move.Jumping(Position(4,4), Position(5,5), Position(6,6))
      .execute(Map(Position(4,4) -> moveMe, Position(5,5) -> captureMe, Position(7,7) -> ignoreMe))
      .get ==
      Map(Position(6,6) -> moveMe, Position(7,7) -> ignoreMe))
  }

  test("jumping move refuses because destination is occupied") {
    assert(Move.Jumping(Position(3,4), Position(4,5), Position(5,6))
      .execute(Map(Position(3,4) -> moveMe, Position(5,6) -> iBlockU))
      .getError ==
      "Position(5,6) is occupied")
  }

  test("jumping move refuses because there's nothing in the 'over' position") {
    assert(Move.Jumping(Position(3,3), Position(4,5), Position(5,7))
      .execute(Map(Position(3,3) -> moveMe, Position(4,4) -> ignoreMe))
      .getError ==
      "Position(4,5) is not occupied by your opponent")
  }

  test("jumping move refuses because what is in the 'over' position isn't capturable") {
    assert(Move.Jumping(Position(3,3), Position(4,4), Position(5,5))
      .execute(Map(Position(3,3) -> moveMe, Position(4,4) -> moveMe))
      .getError ==
      "Position(4,4) is not occupied by your opponent")
  }
}
