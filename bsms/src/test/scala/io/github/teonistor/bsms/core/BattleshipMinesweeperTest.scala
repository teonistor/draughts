package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.core.Orientation.horizontal
import io.github.teonistor.bsms.data.{OwnBoard, PlayerState, Position, ShipDescription}
import io.github.teonistor.bsms.rule.ShipPlacementRule
import io.vavr.control.Validation.valid
import org.mockito.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike

class BattleshipMinesweeperTest extends AnyFunSuiteLike with IdiomaticMockito {

  private val board1 = mock[OwnBoard]
  private val board2 = mock[OwnBoard]
  private val ship = mock[ShipDescription]
  private val position = mock[Position]

  test("place a ship") {
    withObjectMocked[ShipPlacementRule.type] {

      ShipPlacementRule.placeShip(board1, ship, position, horizontal) returns valid(board2)

      val result = new BattleshipMinesweeper(PlayerState(board1, Map.empty, Set.empty, 0))
        .placeShip(1, ship, position, horizontal)
      assert(result.isValid)
      assert(result.get.inspect(-1) == board2)
    }
  }
}
