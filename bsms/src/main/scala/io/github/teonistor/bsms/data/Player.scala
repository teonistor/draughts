package io.github.teonistor.bsms.data

sealed trait Player

object Player {

  val alice: Player = new Player {
    override val toString = "Alice"
  }

  val bob: Player = new Player {
    override val toString = "Bob"
  }
}
