package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.data.GameState
import io.vavr.control.Validation.{invalid, valid}
import org.scalatest.funsuite.AnyFunSuite

class GameOverCheckerTest extends AnyFunSuite {

  test("no (jump in progress)") {
    assert(!new GameOverChecker().isGameOver(GameState(null, null, Some(Vector(7,5))), Map(
      Vector(0, 7) -> Map(
        Vector(2, 1) -> invalid("boo")))))
  }

  test("no (valid targets)") {
    assert(!new GameOverChecker().isGameOver(GameState(null, null, None), Map(
      Vector(0, 0) -> Map(
        Vector(1, 1) -> invalid("boo"),
        Vector(2, 2) -> valid(null)))))
  }

  test("yes (all targets invalid)") {
    assert(new GameOverChecker().isGameOver(GameState(null, null, None), Map(
      Vector(0, 2) -> Map(
        Vector(2, 0) -> invalid("boo")))))
  }

  test("yes (no targets)") { // This shouldn't really happen, but better ensure defence
    assert(new GameOverChecker().isGameOver(GameState(null, null, None), Map(
      Vector(0, 0) -> Map.empty)))
  }

  test("yes (no sources)") {
    assert(new GameOverChecker().isGameOver(GameState(null, null, None), Map.empty))
  }
}
