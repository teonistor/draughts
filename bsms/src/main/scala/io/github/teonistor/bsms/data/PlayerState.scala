package io.github.teonistor.bsms.data

case class PlayerState(board: OwnBoard,
                       opponentBoard: OpponentBoard,
                       shipsToPlace: Set[ShipDescription],
                       minesToPlace: Int)
