package io.github.teonistor.bsms.data

sealed trait Orientation

object Orientation {

  val horizontal: Orientation = new Orientation {}
  val vertical: Orientation = new Orientation {}
}
