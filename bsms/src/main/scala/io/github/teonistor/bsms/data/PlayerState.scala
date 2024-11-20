package io.github.teonistor.bsms.data

case class PlayerState(board: Map[Position, Either[OceanCell, ShipInPlay]],
                       opponentBoard: Map[Position, ShootingEffect],
                       shipsToPlace: Set[ShipDescription],
                       minesToPlace: Int)
