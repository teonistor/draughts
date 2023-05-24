package io.github.teonistor.draughts

sealed trait Player {
  def next: Player
  def isMyPiece(piece:Piece): Boolean
}

object Player {
  val black: Player = new Player {
    def next: Player = white
    def isMyPiece(piece: Piece): Boolean = piece == Piece.blackKing || piece == Piece.blackPeon
  }

  val white: Player = new Player {
    def next: Player = black
    def isMyPiece(piece: Piece): Boolean = piece == Piece.whiteKing || piece == Piece.whitePeon
  }
}

