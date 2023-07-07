package io.github.teonistor.draughts.move

import io.github.teonistor.draughts.data.{GameState, Position}
import io.github.teonistor.draughts.{Piece, Player}
import org.scalatest.funsuite.AnyFunSuite

class MoveTest extends AnyFunSuite {

  // TODO Generalise
  private val moveMe = Piece.whitePeon
  private val ignoreMe = Piece.whitePeon
  private val captureMe = Piece.blackPeon
  private val iBlockU = Piece.whitePeon

  test("sliding move happens") {
    assert(Move.Sliding(Position(2,2), Position(3,3))
      .execute(GameState(Map(Position(2,2) -> moveMe, Position(7,7) -> ignoreMe), Player.white, None))
      .get == GameState(
        Map(Position(3,3) -> moveMe, Position(7,7) -> ignoreMe),
        Player.black,
        None))
  }

  test("sliding move refuses because destination is occupied") {
    assert(Move.Sliding(Position(6,6), Position(5,5))
      .execute(GameState(Map(Position(6,6) -> moveMe, Position(5,5) -> iBlockU), Player.black, None))
      .getError ==
      "(5,5) is occupied")
  }

  test("jumping move happens") {
    assert(Move.Jumping(Position(4,4), Position(5,5), Position(6,6))
      .execute(GameState(Map(Position(4,4) -> moveMe, Position(5,5) -> captureMe, Position(7,7) -> ignoreMe), Player.white, None))
      .get == GameState(
        Map(Position(6,6) -> moveMe, Position(7,7) -> ignoreMe),
        Player.white,
        Some(Position(6,6))))
  }

  test("jumping move refuses because destination is occupied") {
    assert(Move.Jumping(Position(3,4), Position(4,5), Position(5,6))
      .execute(GameState(Map(Position(3,4) -> moveMe, Position(5,6) -> iBlockU), Player.black, None))
      .getError ==
      "(5,6) is occupied")
  }

  test("jumping move refuses because there's nothing in the 'over' position") {
    assert(Move.Jumping(Position(3,3), Position(4,5), Position(5,7))
      .execute(GameState(Map(Position(3,3) -> moveMe, Position(4,4) -> ignoreMe), Player.white, None))
      .getError ==
      "(4,5) is not occupied by your opponent")
  }

  test("jumping move refuses because what is in the 'over' position isn't capturable") {
    assert(Move.Jumping(Position(3,3), Position(4,4), Position(5,5))
      .execute(GameState(Map(Position(3,3) -> moveMe, Position(4,4) -> moveMe), Player.black, None))
      .getError ==
      "(4,4) is not occupied by your opponent")
  }
}
