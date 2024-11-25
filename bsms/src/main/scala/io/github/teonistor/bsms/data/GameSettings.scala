package io.github.teonistor.bsms.data

case class GameSettings(shipsToPlace: Set[ShipDescription],
                        minesToPlace: Int)
