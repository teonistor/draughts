package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.data.Movement.{down, left, right, up}
import org.scalatest.funsuite.AnyFunSuite

class MovementTest extends AnyFunSuite {

  test("Four values") {
    assert(up != null)
    assert(left != null)
    assert(down != null)
    assert(right != null)

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

  private val horizontals = List(
    Set(Vector(2,3), Vector(3,3)),
    Set(Vector(9,5), Vector(7,5), Vector(8,5)))
  private val verticals = List(
    Set(Vector(2,3), Vector(2,4)),
    Set(Vector(9,5), Vector(9,7), Vector(9,6)))
  private val both = List(
    Set(Vector(11, 15)),
    Set(Vector(18, 15)))
  private val neither = List(
    Set(Vector(2,3), Vector(3,4)),
    Set(Vector(9,5), Vector(9), Vector(8,6,4)))

  List(("up", up, verticals ++ both), ("left", left, horizontals ++ both), ("down", down, verticals ++ both), ("right", right, horizontals ++ both))
    .foreach { case (s, movement, aligned) =>
      test(s"Aligned with $s") {
        assert(aligned.forall(movement.aligns))
      }
    }

  List(("up", up, horizontals ++ neither), ("left", left, verticals ++ neither), ("down", down, horizontals ++ neither), ("right", right, verticals ++ neither))
    .foreach { case (s, movement, aligned) =>
      test(s"Not aligned with $s") {
        assert(!aligned.exists(movement.aligns))
      }
    }
}
