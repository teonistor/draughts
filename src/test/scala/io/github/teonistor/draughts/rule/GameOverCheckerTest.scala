package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.data.{GameState, Position}
import io.vavr.control.Validation.{invalid, valid}
import org.scalatest.funsuite.AnyFunSuite

class GameOverCheckerTest extends AnyFunSuite {

  test("no (jump in progress)") {
    assert(!new GameOverChecker().isGameOver(GameState(null, null, Some(Position(7,5))), Map(
      Position(0, 7) -> Map(
        Position(2, 1) -> invalid("boo")))))
  }

  test("no (valid targets)") {
    assert(!new GameOverChecker().isGameOver(GameState(null, null, None), Map(
      Position(0, 0) -> Map(
        Position(1, 1) -> invalid("boo"),
        Position(2, 2) -> valid(null)))))
  }

  test("yes (all targets invalid)") {
    assert(new GameOverChecker().isGameOver(GameState(null, null, None), Map(
      Position(0, 2) -> Map(
        Position(2, 0) -> invalid("boo")))))
  }

  test("yes (no targets)") { // This shouldn't really happen, but better ensure defence
    assert(new GameOverChecker().isGameOver(GameState(null, null, None), Map(
      Position(0, 0) -> Map.empty)))
  }

  test("yes (no sources)") {
    assert(new GameOverChecker().isGameOver(GameState(null, null, None), Map.empty))
  }
}
