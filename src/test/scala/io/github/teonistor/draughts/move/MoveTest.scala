package io.github.teonistor.draughts.move

import io.github.teonistor.draughts.data.GameState
import io.github.teonistor.draughts.{Piece, Player}
import org.scalatest.funsuite.AnyFunSuite

class MoveTest extends AnyFunSuite {

  // TODO Generalise
  private val moveMe = Piece.whitePeon
  private val ignoreMe = Piece.whitePeon
  private val captureMe = Piece.blackPeon
  private val iBlockU = Piece.whitePeon

  test("sliding move happens") {
    assert(Move.Sliding(Vector(2,2), Vector(3,3))
      .execute(GameState(Map(Vector(2,2) -> moveMe, Vector(7,7) -> ignoreMe), Player.white, None))
      .get == GameState(
        Map(Vector(3,3) -> moveMe, Vector(7,7) -> ignoreMe),
        Player.black,
        None))
  }

  test("sliding move refuses because destination is occupied") {
    assert(Move.Sliding(Vector(6,6), Vector(5,5))
      .execute(GameState(Map(Vector(6,6) -> moveMe, Vector(5,5) -> iBlockU), Player.black, None))
      .getError ==
      "(5,5) is occupied")
  }

  test("jumping move happens") {
    assert(Move.Jumping(Vector(4,4), Vector(5,5), Vector(6,6))
      .execute(GameState(Map(Vector(4,4) -> moveMe, Vector(5,5) -> captureMe, Vector(7,7) -> ignoreMe), Player.white, None))
      .get == GameState(
        Map(Vector(6,6) -> moveMe, Vector(7,7) -> ignoreMe),
        Player.white,
        Some(Vector(6,6))))
  }

  test("jumping move refuses because destination is occupied") {
    assert(Move.Jumping(Vector(3,4), Vector(4,5), Vector(5,6))
      .execute(GameState(Map(Vector(3,4) -> moveMe, Vector(5,6) -> iBlockU), Player.black, None))
      .getError ==
      "(5,6) is occupied")
  }

  test("jumping move refuses because there's nothing in the 'over' Vector") {
    assert(Move.Jumping(Vector(3,3), Vector(4,5), Vector(5,7))
      .execute(GameState(Map(Vector(3,3) -> moveMe, Vector(4,4) -> ignoreMe), Player.white, None))
      .getError ==
      "(4,5) is not occupied by your opponent")
  }

  test("jumping move refuses because what is in the 'over' Vector isn't capturable") {
    assert(Move.Jumping(Vector(3,3), Vector(4,4), Vector(5,5))
      .execute(GameState(Map(Vector(3,3) -> moveMe, Vector(4,4) -> moveMe), Player.black, None))
      .getError ==
      "(4,4) is not occupied by your opponent")
  }
}
