package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.core.Orientation.horizontal
import io.github.teonistor.bsms.rule.ShipPlacementRule
import io.vavr.control.Validation.{invalid, valid}
import org.apache.commons.lang3.RandomUtils.nextInt
import org.mockito.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike

class PlayerStateTest extends AnyFunSuiteLike with IdiomaticMockito {

  private val board1 = mock[OwnBoard]
  private val board2 = mock[OwnBoard]
  private val ship = mock[ShipDescription]
  private val position = mock[Position]
  private val movement = mock[Vector[Int]]

  test("place a ship") {
    withObjectMocked[ShipPlacementRule.type] {

      ShipPlacementRule.placeShip(board1, ship, position, horizontal) returns valid(board2)

      val result = PlayerState(board1, Map.empty, Set.empty, 0).placeShip(ship, position, horizontal)
      assert(result.isValid)
      assert(result.get.board == board2)
    }
  }

  test("cannot place ship") {
    withObjectMocked[ShipPlacementRule.type] {

      ShipPlacementRule.placeShip(board1, ship, position, horizontal) returns invalid("Some reason")

      val result = PlayerState(board1, Map.empty, Set.empty, 0).placeShip(ship, position, horizontal)
      assert(result.isInvalid)
      assert(result.getError == "Some reason")
    }
  }

  test("move a ship") {
    withObjectMocked[ShipPlacementRule.type] {

      ShipPlacementRule.moveShip(board1, position, movement) returns valid(board2)

      val result = PlayerState(board1, Map.empty, Set.empty, 0).moveShip(position, movement)
      assert(result.isValid)
      assert(result.get.board == board2)
    }
  }

  test("cannot move ship") {
    withObjectMocked[ShipPlacementRule.type] {

      ShipPlacementRule.moveShip(board1, position, movement) returns invalid("Some other reason")

      val result = PlayerState(board1, Map.empty, Set.empty, 0).moveShip(position, movement)
      assert(result.isInvalid)
      assert(result.getError == "Some other reason")
    }
  }

  test("use a ship") {
    val nautilus = ShipDescription("Nautilus", 3)

    val result = PlayerState(Map.empty, Map.empty, Set(nautilus, ship), 0)
      .useShip(ship)
    assert(result.isValid)
    assert(result.get.shipsToPlace == Set(nautilus))
  }

  test("cannot use a ship you don't have") {
    val nautilus = ShipDescription("Nautilus", 3)
    val titanic = ShipDescription("Titanic", 5)

    List(ship,
        ShipDescription("Atlantis", 5),
        ShipDescription("Eleanor", 3),
        ShipDescription("Nautilus", 5),
        ShipDescription("Titanic", 3))
      .map(ship => PlayerState(Map.empty, Map.empty, Set(nautilus, titanic), 0)
        .useShip(ship))
      .foreach(result => {
        assert(result.isInvalid)
        assert(result.getError == "Cannot use a ship you do not have")
      })
  }

  test("use a mine") {
    val mines = nextInt(1, 100)

    val result = PlayerState(Map.empty, Map.empty, Set.empty, mines + 1).useMine()
    assert(result.isValid)
    assert(result.get.minesToPlace == mines)
  }

  test("cannot use a mine you don't have") {
    val result = PlayerState(Map.empty, Map.empty, Set.empty, 0).useMine()
    assert(result.isInvalid)
    assert(result.getError == "Cannot use a mine you do not have")
  }
}
