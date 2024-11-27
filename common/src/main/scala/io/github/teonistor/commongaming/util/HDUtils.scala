package io.github.teonistor.commongaming.util

import scala.annotation.tailrec

object HDUtils {

  def cartesianProduct[T](movements:IndexedSeq[Iterable[T]]): Vector[Vector[T]] =
    cartesianProduct0(Vector(Vector.fill(movements.size)(null.asInstanceOf[T])), movements, 0)

  @tailrec
  private def cartesianProduct0[T](accum: Vector[Vector[T]], movements: IndexedSeq[Iterable[T]], i: Int): Vector[Vector[T]] =
    if (i >= movements.size)
      accum
    else
      cartesianProduct0(accum.flatMap(v => movements(i).map(d => v.updated(i, d))), movements, i + 1)
}
