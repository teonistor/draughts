package io.github.teonistor.bsms.core

sealed trait Orientation

object Orientation {

  val horizontal: Orientation = new Orientation {}
  val vertical: Orientation = new Orientation {}
}
