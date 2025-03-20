package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.data.Movement.{down, left, right, up}
import org.scalatest.funsuite.AnyFunSuite

class MovementTest extends AnyFunSuite {

  test("Four values") {
    assert(up != null)
    assert(Movement.left != null)
    assert(Movement.down != null)
    assert(Movement.right != null)

    assertDoesNotCompile("new Movement {}")
  }

  Vector(
      2, 7,   2, 6,  1, 7,  2, 8,  3, 7,
      1, 8,   1, 7,  0, 8,  1, 9,  2, 8,
      6, 3,   6, 2,  5, 3,  6, 4,  7, 3)
    .grouped(10)
    .map(_.grouped(2).toList)
    .flatMap { case input :: outUp :: outLeft :: outDown :: outRight :: Nil =>
      List(("up", up, input, outUp), ("left", left, input, outLeft), ("down", down, input, outDown), ("right", right, input, outRight)) }
    .foreach { case (s, movement, input, output) =>

      test(s"Move $input $s to $output") {
        assert(movement.move(input) == output)
      }
    }
}
