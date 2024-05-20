package io.github.teonistor.ttt

sealed trait Player {
  def next: Player
}

object Player {
  val X: Player = new Player {
    override def next: Player = O
    override def toString: String = "X"
  }

  val O: Player = new Player {
    override def next: Player = X
    override def toString: String = "O"
  }
}
