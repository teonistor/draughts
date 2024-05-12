package io.github.teonistor.connectn.data

import io.github.teonistor.connectn.Game
import io.github.teonistor.connectn.data.Color.{Red, Yellow}
import io.github.teonistor.connectn.data.GameState.Board
import io.github.teonistor.draughts.HDUtils
import io.github.teonistor.draughts.HDUtils.cartesianProduct
import org.scalatest.funsuite.AnyFunSuiteLike

class GameOverCheckerTest extends AnyFunSuiteLike {
  private val standardSettings = GameSettings(Vector(7), 6, 4)

  private def hackly(board:Board, settings: GameSettings) = {
    // TODO Unhack
    val list = board.toList
    val hack = list.tail.prepended((list.head._1, list.head._2.tail)).toMap

    GameOverChecker.isGameOver(new Game(settings, List(GameState(board, Red), GameState(
      hack,
      Yellow))))
  }

  test("Make sure cartesianProduct() does what we expect") {
    println(cartesianProduct(Vector(Vector(2, 3), Vector(1, 4, 7))))
  }

  test("Empty") {
    assert(GameOverChecker.isGameOver(new Game(standardSettings, GameState(Map.empty, Red))) == (false, None))
  }

  test("Vertical") {
    assert(hackly(Map(Vector(4) -> List(Red, Red, Red, Red)), standardSettings) == (true, Some(Red)))
  }

  test("No") {
    assert(hackly(Map(Vector(4) -> List(Red, Red, Yellow, Red, Red)), standardSettings) == (false, None))
  }

  test("Horizontal") {
    assert(hackly(Map(
      Vector(2) -> List(Red),
      Vector(3) -> List(Red),
      Vector(4) -> List(Red),
      Vector(5) -> List(Red)), standardSettings) == (true, Some(Red)))
  }

  test("Diagonal up 1") {
    assert(hackly(Map(
      Vector(2) -> List(Yellow),
      Vector(3) -> List(Yellow, Red),
      Vector(4) -> List(Yellow, Yellow, Red),
      Vector(5) -> List(Yellow, Red, Red, Red)), standardSettings) == (true, Some(Yellow)))
  }

  test("Diagonal down 1") {
    assert(hackly(Map(
      Vector(5) -> List(Yellow, Red, Red, Red),
      Vector(4) -> List(Yellow, Yellow, Red),
      Vector(3) -> List(Yellow, Red),
      Vector(2) -> List(Yellow)), standardSettings) == (true, Some(Yellow)))
  }

  test("Stalemante") {
    assert(hackly(Map(
      Vector(0, 0) -> List(Red, Yellow),
      Vector(0, 1) -> List(Yellow, Red),
      Vector(1, 0) -> List(Yellow, Red),
      Vector(1, 1) -> List(Red, Yellow)), GameSettings(Vector(2, 2), 2, 2)) == (true, None))
  }
}
