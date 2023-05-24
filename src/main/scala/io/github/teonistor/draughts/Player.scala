package io.github.teonistor.draughts

sealed trait Player {
  def next: Player
}

object Player {
  val black: Player = new Player {
    def next: Player = white
  }

  val white: Player = new Player {
    def next: Player = black
  }
}

