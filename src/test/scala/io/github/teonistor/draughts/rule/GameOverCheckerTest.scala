package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.data.Position
import io.vavr.control.Validation.{invalid, valid}
import org.scalatest.funsuite.AnyFunSuite

class GameOverCheckerTest extends AnyFunSuite {

  test("no") {
    assert(!new GameOverChecker().isGameOver(Map(
      Position(0, 0) -> Map(
        Position(1, 1) -> invalid("boo"),
        Position(2, 2) -> valid(Map.empty)))))
  }

  test("yes (all targets invalid)") {
    assert(new GameOverChecker().isGameOver(Map(
      Position(0, 2) -> Map(
        Position(2, 0) -> invalid("boo")))))
  }

  test("yes (no targets)") { // This shouldn't really happen, but better ensure defence
    assert(new GameOverChecker().isGameOver(Map(
      Position(0, 0) -> Map.empty)))
  }

  test("yes (no sources)") {
    assert(new GameOverChecker().isGameOver(Map.empty))
  }
}
