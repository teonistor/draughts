package io.github.teonistor.draughts.move

import io.github.teonistor.draughts.Position
import org.scalatest.funsuite.AnyFunSuite

class MoveTest extends AnyFunSuite {

  test("sliding move happens") {
    assert(Move.Sliding(Position(2,2), Position(3,3))
      .execute(Map(Position(2,2) -> "move me", Position(7,7) -> "leave me alone"))
      .get ==
      Map(Position(3,3) -> "move me", Position(7,7) -> "leave me alone"))
  }

  test("sliding move refuses because destination is occupied") {
    assert(Move.Sliding(Position(6,6), Position(5,5))
      .execute(Map(Position(6,6) -> "try to move me", Position(5,5) -> "i block u"))
      .getError ==
      "Position(5,5) is occupied")
  }

  test("jumping move happens") {
    assert(Move.Jumping(Position(4,4), Position(5,5), Position(6,6))
      .execute(Map(Position(4,4) -> "move me", Position(5,5) -> "capture me", Position(7,7) -> "leave me alone"))
      .get ==
      Map(Position(6,6) -> "move me", Position(7,7) -> "leave me alone"))
  }

  test("jumping move refuses because destination is occupied") {
    assert(Move.Jumping(Position(3,4), Position(4,5), Position(5,6))
      .execute(Map(Position(3,4) -> "try to move me", Position(5,6) -> "i block u"))
      .getError ==
      "Position(5,6) is occupied")
  }

  test("jumping move refuses because there's nothing in the 'over' position") {
    assert(Move.Jumping(Position(3,3), Position(4,5), Position(5,7))
      .execute(Map(Position(3,3) -> "try to move me", Position(4,4) -> "sitting duck"))
      .getError ==
      "Position(4,5) is not occupied by your opponent")
  }

  test("jumping move refuses what is in the 'over' position isn't capturable") {
    assert(Move.Jumping(Position(3,3), Position(4,4), Position(5,5))
      .execute(Map(Position(3,3) -> "try to move me", Position(4,4) -> "friendly fire"))
      .getError ==
      "Position(4,4) is not occupied by your opponent")
  }
}
