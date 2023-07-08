package io.github.teonistor.draughts

import scala.annotation.tailrec

object HDUtils {

  def cartesianProduct(movements:IndexedSeq[Iterable[Int]]): Vector[Vector[Int]] =
    cartesianProduct0(Vector(Vector.fill(movements.size)(0)), movements, 0)

  @tailrec
  private def cartesianProduct0(accum: Vector[Vector[Int]], movements: IndexedSeq[Iterable[Int]], i: Int): Vector[Vector[Int]] =
    if (i >= movements.size)
      accum
    else
      cartesianProduct0(accum.flatMap(v => movements(i).map(d => v.updated(i, v(i) + d))), movements, i + 1)
}
