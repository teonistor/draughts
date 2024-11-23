package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.data._

class BattleshipMinesweeper(val playerState: PlayerState) {

  def placeShip(player: Int, ship: ShipDescription, position: Position, orientation: Orientation): ValidatedGame =
    playerState.placeShip(ship, position, orientation)
      .map(new BattleshipMinesweeper(_))

  def moveShip(player: Int, position: Position, movement: Vector[Int]):ValidatedGame =
    playerState.moveShip(position, movement)
      .map(new BattleshipMinesweeper(_))

  def useShip(player: Int, ship: ShipDescription):ValidatedGame =
     playerState.useShip(ship)
      .map(new BattleshipMinesweeper(_))

  def useMine(player: Int):ValidatedGame =
     playerState.useMine()
      .map(new BattleshipMinesweeper(_))
}
