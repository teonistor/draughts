package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{Position, Settings}

class InitialBoardProvider {
  def createBoard(settings: Settings): Map[Position,Piece] = {

    val mkRow = (piece: Piece) => (lastCoordinate: Iterable[Int]) =>
      HDUtils.cartesianProduct(Vector.tabulate(settings.boardSizes.size)(i =>
           if (i < settings.boardSizes.size - 1) 0 until settings.boardSizes(i)
           else lastCoordinate))
        .filter(_.sum % 2 == 0)
        .map((_, piece))

    mkRow(Piece.blackPeon)((1 to settings.startingRows).map(settings.boardSizes.last - _))
      .++(mkRow(Piece.whitePeon)(0 until settings.startingRows))
      .toMap
  }
}
