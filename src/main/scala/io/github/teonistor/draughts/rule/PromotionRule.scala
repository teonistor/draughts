package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.data.{Position, Settings}
import io.github.teonistor.draughts.{Piece, Player}

class PromotionRule(settings: Settings) {
  def promoteAsNeeded(in:  Map[Position, Piece]):  Map[Position, Piece] =
    in.map {
      case (position,piece) =>
        if (position.y == 0 && Player.black.isMyPiece(piece)
         || position.y == settings.boardHeight - 1 && Player.white.isMyPiece(piece))
             (position, piece.promote)
        else (position, piece)
    }
}
