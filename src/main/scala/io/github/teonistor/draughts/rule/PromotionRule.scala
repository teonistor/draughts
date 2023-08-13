package io.github.teonistor.draughts.rule

import io.github.teonistor.draughts.data.Settings
import io.github.teonistor.draughts.{Piece, Player}

class PromotionRule(settings: Settings) {
  def promoteAsNeeded(in: Map[Vector[Int], Piece]): Map[Vector[Int], Piece] =
    in.map {
      case (position, piece) =>
        if (position.last == 0 && Player.black.isMyPiece(piece)
         || position.last == settings.boardSizes.last - 1 && Player.white.isMyPiece(piece))
             (position, piece.promote)
        else (position, piece)
    }
}
