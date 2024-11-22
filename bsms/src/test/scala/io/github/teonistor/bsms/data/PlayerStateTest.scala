package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.core.Orientation.horizontal
import io.github.teonistor.bsms.rule.ShipPlacementRule
import io.vavr.control.Validation.valid
import org.mockito.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike

class PlayerStateTest extends AnyFunSuiteLike with IdiomaticMockito {

  private val board1 = mock[OwnBoard]
  private val board2 = mock[OwnBoard]
  private val ship = mock[ShipDescription]
  private val position = mock[Position]

  test("place a ship") {
    withObjectMocked[ShipPlacementRule.type] {

      ShipPlacementRule.placeShip(board1, ship, position, horizontal) returns valid(board2)

      val result = PlayerState(board1, Map.empty, Set.empty, 0)
        .placeShip(ship, position, horizontal)
      assert(result.isValid)
      assert(result.get.board == board2)
    }
  }
}
