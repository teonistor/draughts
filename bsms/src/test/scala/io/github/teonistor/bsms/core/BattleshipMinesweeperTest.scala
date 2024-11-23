package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.data.{PlayerState, Position, ShipDescription}
import io.vavr.control.Validation.{invalid, valid}
import org.mockito.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike

class BattleshipMinesweeperTest extends AnyFunSuiteLike with IdiomaticMockito {

  private val aliceBefore = mock[PlayerState]
  private val aliceAfter = mock[PlayerState]
  private val bobBefore = mock[PlayerState]
  private val bobAfter = mock[PlayerState]
  private val ship = mock[ShipDescription]
  private val position = mock[Position]
  private val orientation = mock[Orientation]
  private val movement = mock[Vector[Int]]

  test("place a ship") {
    state1.placeShip(ship, position, orientation) returns valid(state2)

    val result = new BattleshipMinesweeper(state1).placeShip(1, ship, position, orientation)
    assert(result.isValid)
    assert(result.get.playerState == state2)
  }

  test("cannot place ship") {
    state1.placeShip(ship, position, orientation) returns invalid("Some reason")

    val result = new BattleshipMinesweeper(state1).placeShip(1, ship, position, orientation)
    assert(result.isInvalid)
    assert(result.getError == "Some reason")
  }

  test("move a ship") {
    bobBefore.moveShip(position, movement) returns valid(bobAfter)

    val result = new BattleshipMinesweeper(bobBefore).moveShip(1, position, movement)
    assert(result.isValid)
    assert(result.get.playerState == bobAfter)
  }

  test("cannot move ship") {
    bobBefore.moveShip(position, movement) returns invalid("Some second reason")

    val result = new BattleshipMinesweeper(bobBefore).moveShip(1, position, movement)
    assert(result.isInvalid)
    assert(result.getError == "Some second reason")
  }

  test("use a ship") {
    state1.useShip(ship) returns valid(state2)

    val result = new BattleshipMinesweeper(state1).useShip(1, ship)
    assert(result.isValid)
    assert(result.get.playerState == state2)
  }

  test("cannot use ship") {
    state1.useShip(ship) returns invalid("Some other reason")

    val result = new BattleshipMinesweeper(state1).useShip(1, ship)
    assert(result.isInvalid)
    assert(result.getError == "Some other reason")
  }

  test("use a mine") {
    state1.useMine() returns valid(state2)

    val result = new BattleshipMinesweeper(state1).useMine(1)
    assert(result.isValid)
    assert(result.get.playerState == state2)
  }

  test("cannot use mine") {
    state1.useMine() returns invalid("Final reason")

    val result = new BattleshipMinesweeper(state1).useMine(1)
    assert(result.isInvalid)
    assert(result.getError == "Final reason")
  }
}
