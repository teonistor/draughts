package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.data.Player.{alice, bob}
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
    aliceBefore.placeShip(ship, position, orientation) returns valid(aliceAfter)

    val result = new BattleshipMinesweeper(aliceBefore,bobBefore).placeShip(alice, ship, position, orientation)
    assert(result.isValid)
    assert(result.get.aliceState == aliceAfter)
    assert(result.get.bobState == bobBefore)
  }

  test("cannot place ship") {
    aliceBefore.placeShip(ship, position, orientation) returns invalid("Some reason")

    val result = new BattleshipMinesweeper(aliceBefore,bobBefore).placeShip(alice, ship, position, orientation)
    assert(result.isInvalid)
    assert(result.getError == "Some reason")
  }

  test("move a ship") {
    bobBefore.moveShip(position, movement) returns valid(bobAfter)

    val result = new BattleshipMinesweeper(aliceBefore,bobBefore).moveShip(bob, position, movement)
    assert(result.isValid)
    assert(result.get.aliceState == aliceBefore)
    assert(result.get.bobState == bobAfter)
  }

  test("cannot move ship") {
    bobBefore.moveShip(position, movement) returns invalid("Some second reason")

    val result = new BattleshipMinesweeper(aliceBefore,bobBefore).moveShip(bob, position, movement)
    assert(result.isInvalid)
    assert(result.getError == "Some second reason")
  }

  test("use a ship") {
    aliceBefore.useShip(ship) returns valid(aliceAfter)

    val result = new BattleshipMinesweeper(aliceBefore,bobBefore).useShip(alice, ship)
    assert(result.isValid)
    assert(result.get.aliceState == aliceAfter)
    assert(result.get.bobState == bobBefore)
  }

  test("cannot use ship") {
    aliceBefore.useShip(ship) returns invalid("Some other reason")

    val result = new BattleshipMinesweeper(aliceBefore,bobBefore).useShip(alice, ship)
    assert(result.isInvalid)
    assert(result.getError == "Some other reason")
  }

  test("use a mine") {
    bobBefore.useMine() returns valid(bobAfter)

    val result = new BattleshipMinesweeper(aliceBefore,bobBefore).useMine(bob)
    assert(result.isValid)
    assert(result.get.aliceState == aliceBefore)
    assert(result.get.bobState == bobAfter)
  }

  test("cannot use mine") {
    bobBefore.useMine() returns invalid("Final reason")

    val result = new BattleshipMinesweeper(aliceBefore,bobBefore).useMine(bob)
    assert(result.isInvalid)
    assert(result.getError == "Final reason")
  }
}
