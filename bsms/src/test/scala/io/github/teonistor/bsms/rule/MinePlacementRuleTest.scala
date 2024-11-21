package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.data.OceanCell.mine
import org.scalatest.funsuite.AnyFunSuiteLike

class MinePlacementRuleTest extends AnyFunSuiteLike {

  test("place a mine") {
    val position = Vector(3, 7)

    val result = MinePlacementRule.placeMine(Map.empty, position)

    assert(result.isValid)
    assert(result.get == Map(position -> Left(mine)))

// What to do about keeping info about the opponent's board isn't so straightforward; deal with it later
//    assert(result.get._1 == Map(position -> Left(mine)))
//    assert(result.get._2 == Map(position -> miss))
  }

}
