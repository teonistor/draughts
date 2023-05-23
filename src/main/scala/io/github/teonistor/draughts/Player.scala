package io.github.teonistor.draughts

sealed trait Player {

}

object Player {
  val black = new Player{}
  val white = new Player{}
}

