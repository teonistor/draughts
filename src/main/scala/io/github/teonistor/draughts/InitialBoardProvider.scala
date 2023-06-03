package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{Position, Settings}

class InitialBoardProvider {
  def createBoard(settings: Settings): Map[Position,Piece] = {

    val mkRow = (piece: Piece) => (y: Int) => (0 until settings.boardWidth)
      .filter(x => (x + y) % 2 == 0)
      .map(x => (Position(x, y), piece))

    ( (1 to settings.startingRows).map(settings.boardHeight -_)
        .flatMap(mkRow(Piece.blackPeon)) ++
      (0 until settings.startingRows)
        .flatMap(mkRow(Piece.whitePeon)))
      .toMap
  }
}
