package io.github.teonistor.connectn

import io.github.teonistor.connectn.data.Color.{Red, Yellow}
import io.github.teonistor.connectn.data.{GameOverChecker, GameSettings, GameState}
import org.mockito.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike

class GameTest extends IdiomaticMockito with AnyFunSuiteLike {

  test("Cannot create without a starting state") {
    val result = intercept[IllegalArgumentException](new Game(GameSettings(Vector(4), 6, 4), List.empty))

    assert(result.getMessage == "Cannot create game without a starting state")
  }

  test("invalid - dimensionality 1!=2") {
    val actual = new Game(
        GameSettings(Vector(4, 4), 6, 4),
        GameState(Map.empty, Red))
      .placePieceAt(Vector(3))

    assert(actual.getError == "Invalid 1-dimensional input in game with 2-dimensional base")
  }

  test("invalid - dimensionality 3!=1") {
    val actual = new Game(
        GameSettings(Vector(5), 6, 4),
        GameState(Map.empty, Red))
      .placePieceAt(Vector(3, 7, 11))

    assert(actual.getError == "Invalid 3-dimensional input in game with 1-dimensional base")
  }

  test("invalid - too low") {
    val actual = new Game(
        GameSettings(Vector(5, 5), 6, 4),
        GameState(Map.empty, Red))
      .placePieceAt(Vector(-1, 2))

    assert(actual.getError == "Invalid input -1<0")
  }

  test("invalid - too high") {
    val actual = new Game(
        GameSettings(Vector(5, 5), 6, 4),
        GameState(Map.empty, Red))
      .placePieceAt(Vector(3, 6))

    assert(actual.getError == "Invalid input 6>=5")
  }

  test("invalid - column full 1d") {
    val actual = new Game(
        GameSettings(Vector(6), 3, 4),
        GameState(Map(Vector(2) -> List(Red, Red, Red)), Red))
      .placePieceAt(Vector(2))

    assert(actual.getError == "Invalid input: column 2 is full")
  }

  test("invalid - column full 2d") {
    val actual = new Game(
        GameSettings(Vector(5, 5), 3, 4),
        GameState(Map(Vector(3, 4) -> List(Red, Red, Red)), Red))
      .placePieceAt(Vector(3, 4))

    assert(actual.getError == "Invalid input: column (3,4) is full")
  }

  test("invalid - game over") {
    val game = new Game(
      GameSettings(Vector(5, 5), 3, 4),
      GameState(Map.empty, Red))

    withObjectMocked[GameOverChecker.type] {
      GameOverChecker.isGameOver(game).returns((true, None))
      val actual = game.placePieceAt(Vector(2, 1))

      assert(actual.getError == "Game over")
    }
  }

  test("perform move on empty column") {
    val game = new Game(
      GameSettings(Vector(5), 5, 4),
      GameState(Map(Vector(1) -> List(Yellow)), Red))

    withObjectMocked[GameOverChecker.type] {
      GameOverChecker.isGameOver(game).returns((false, None))
      val actual = game.placePieceAt(Vector(3))

      assert(actual.get.currentState.board == Map(Vector(1) -> List(Yellow), Vector(3) -> List(Red)))
      assert(actual.get.currentState.currentPlayer == Yellow)
    }
  }

  test("perform move nonempty column") {
    val game = new Game(
      GameSettings(Vector(5), 5, 4),
      GameState(Map(Vector(1) -> List(Yellow), Vector(3) -> List(Red)), Red))

    withObjectMocked[GameOverChecker.type] {
      GameOverChecker.isGameOver(game).returns((false, None))
      val actual = game.placePieceAt(Vector(3))

      assert(actual.get.currentState.board == Map(Vector(1) -> List(Yellow), Vector(3) -> List(Red, Red)))
      assert(actual.get.currentState.currentPlayer == Yellow)
    }
  }
}
