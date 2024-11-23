package io.github.teonistor.bsms.data

sealed trait Player {
  val other: Player
}

object Player {

  val alice: Player = new Player {
    lazy val other: Player = bob
    override val toString = "Alice"
  }

  val bob: Player = new Player {
    lazy val other: Player = alice
    override val toString = "Bob"
  }
}
