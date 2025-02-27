package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.data.Player.{alice, bob}
import io.github.teonistor.bsms.data._
import io.github.teonistor.bsms.rule.{GameOverChecker, StageChange}
import io.vavr.control.Validation.{invalid, valid}
import org.mockito.ArgumentMatchers.any
import org.mockito.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike

class BattleshipMinesweeperTest extends AnyFunSuiteLike with IdiomaticMockito {

  private val settings = mock[GameSettings]
  private val aliceBefore = mock[PlayerState]
  private val aliceMiddle = mock[PlayerState]
  private val aliceAfter = mock[PlayerState]
  private val bobBefore = mock[PlayerState]
  private val bobAfter = mock[PlayerState]
  private val ship = mock[ShipDescription]
  private val position = mock[Position]
  private val orientation = mock[Orientation]
  private val movement = mock[Vector[Int]]

  test("place and use a ship") {
    aliceBefore.placeShip(ship, position, orientation) returns valid(aliceMiddle)
    aliceMiddle.useShip(ship) returns valid(aliceAfter)
    withObjectMocked[StageChange.type] {
      StageChange.advanceStageIfNecessary(any()) answers ((a: BattleshipMinesweeper) => a)

      val result = BattleshipMinesweeper(settings, aliceBefore, bobBefore).placeShip(alice, ship, position, orientation)

      assert(result.isValid)
      assert(result.get.aliceState == aliceAfter)
      assert(result.get.bobState == bobBefore)
      StageChange.advanceStageIfNecessary(any()) wasCalled once
    }
  }

  test("cannot place ship") {
    aliceBefore.placeShip(ship, position, orientation) returns invalid("Some reason")

    val result = BattleshipMinesweeper(settings, aliceBefore, bobBefore).placeShip(alice, ship, position, orientation)
    assert(result.isInvalid)
    assert(result.getError == "Some reason")
  }

  test("cannot use ship") {
    aliceBefore.placeShip(ship, position, orientation) returns valid(aliceMiddle)
    aliceMiddle.useShip(ship) returns invalid("Some other reason")

    val result = BattleshipMinesweeper(settings, aliceBefore, bobBefore).placeShip(alice, ship, position, orientation)
    assert(result.isInvalid)
    assert(result.getError == "Some other reason")
  }

  test("move a ship") {
    bobBefore.moveShip(position, movement) returns valid(bobAfter)
    withObjectMocked[StageChange.type] {
      StageChange.advanceStageIfNecessary(any()) answers ((a: BattleshipMinesweeper) => a)

      val result = BattleshipMinesweeper(settings, aliceBefore, bobBefore).moveShip(bob, position, movement)

      assert(result.isValid)
      assert(result.get.aliceState == aliceBefore)
      assert(result.get.bobState == bobAfter)
      StageChange.advanceStageIfNecessary(any()) wasCalled once
    }
  }

  test("cannot move ship") {
    bobBefore.moveShip(position, movement) returns invalid("Some second reason")

    val result = BattleshipMinesweeper(settings, aliceBefore, bobBefore).moveShip(bob, position, movement)
    assert(result.isInvalid)
    assert(result.getError == "Some second reason")
  }

  test("skip ship movement") {
    bobBefore.pass() returns bobAfter

    val result = BattleshipMinesweeper(settings, aliceBefore, bobBefore).pass(bob)
    assert(result.bobState == bobAfter)
  }

  test("use a mine and place it on the other player's board") {
    bobBefore.useMine() returns valid(bobAfter)
    aliceBefore.placeMine(position) returns aliceAfter
    withObjectMocked[StageChange.type] {
      StageChange.advanceStageIfNecessary(any()) answers ((a: BattleshipMinesweeper) => a)

      val result = BattleshipMinesweeper(settings, aliceBefore, bobBefore).placeMine(bob, position)

      assert(result.isValid)
      assert(result.get.aliceState == aliceAfter)
      assert(result.get.bobState == bobAfter)
      StageChange.advanceStageIfNecessary(any()) wasCalled twice
    }
  }

  test("cannot use mine") {
    bobBefore.useMine() returns invalid("Final reason")

    val result = BattleshipMinesweeper(settings, aliceBefore, bobBefore).placeMine(bob, position)
    assert(result.isInvalid)
    assert(result.getError == "Final reason")
  }

  test("shoot") {
    aliceBefore.placeMine(position) returns aliceAfter
    bobBefore.shoot() returns valid(bobAfter)
    withObjectMocked[StageChange.type] {
      StageChange.advanceStageIfNecessary(any()) answers ((a: BattleshipMinesweeper) => a)

      val result = BattleshipMinesweeper(settings, aliceBefore, bobBefore).shoot(bob, position)

      assert(result.isValid)
      assert(result.get.aliceState == aliceAfter)
      assert(result.get.bobState == bobAfter)
      StageChange.advanceStageIfNecessary(any()) wasCalled twice
    }
  }

  test("cannot shoot") {
    aliceBefore.placeMine(position) returns aliceAfter
    bobBefore.shoot() returns invalid("Sting operation")
    withObjectMocked[StageChange.type] {
      StageChange.advanceStageIfNecessary(any()) answers ((a: BattleshipMinesweeper) => a)

      val result = BattleshipMinesweeper(settings, aliceBefore, bobBefore).shoot(bob, position)

      assert(result.isInvalid)
      assert(result.getError == "Sting operation")
      StageChange.advanceStageIfNecessary(any()) wasCalled once
    }
  }

  test("Game Condition") {
    val game = BattleshipMinesweeper(settings, aliceBefore, bobBefore)
    val condition = mock[GameCondition]
    withObjectMocked[GameOverChecker.type] {
      GameOverChecker.check(game) returns condition

      assert(game.condition == condition)
    }
  }
}
