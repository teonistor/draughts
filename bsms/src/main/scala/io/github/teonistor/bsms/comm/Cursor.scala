package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.data.Position

sealed trait Cursor {
  def ownCursor: Option[Position] = None
  def opponentCursor: Option[Position] = None
}

object Cursor {
  val none: Cursor = new Cursor {}

  def own(position: Position): Cursor = new Cursor {
    override val ownCursor: Option[Position] = Some(position)
  }

  def opponent(position: Position): Cursor = new Cursor {
    override val opponentCursor: Option[Position] = Some(position)
  }
}
