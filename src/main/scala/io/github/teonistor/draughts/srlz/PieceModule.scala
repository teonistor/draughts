package io.github.teonistor.draughts.srlz

import io.github.teonistor.draughts.Piece

object PieceModule extends EnumeratedTypeModule("PieceModule", Map(
  Piece.blackKing -> "BK",
  Piece.blackPeon -> "BP",
  Piece.whiteKing -> "WK",
  Piece.whitePeon -> "WP")) {}
