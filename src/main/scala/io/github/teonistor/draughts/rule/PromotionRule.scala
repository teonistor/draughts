package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.data.Position
import io.github.teonistor.draughts.{Piece, Player}

class PromotionRule {
  def promoteAsNeeded(in:  Map[Position, Piece]):  Map[Position, Piece] =
    in.map {
      case (position,piece) =>
        if      (position.y == 0 && Player.black.isMyPiece(piece)) (position, piece.promote)
        else if (position.y == 7 && Player.white.isMyPiece(piece)) (position, piece.promote)
        else    (position, piece)
    }
}
