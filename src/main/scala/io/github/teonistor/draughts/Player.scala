package io.github.teonistor.draughts

sealed trait Player {
  def next: Player
  def isMyPiece(piece:Piece): Boolean
}

object Player {
  val black: Player = new Player {
    def next: Player = white
    override def isMyPiece(piece: Piece): Boolean = ???
  }

  val white: Player = new Player {
    def next: Player = black
    override def isMyPiece(piece: Piece): Boolean = ???
  }
}

