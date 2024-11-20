package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.data._
import io.vavr.control.Validation.valid

class BattleshipMinesweeper(playerState: PlayerState) {

  def placeShip(player: Int, position: Position, ship: ShipDescription, horizontal: Boolean): ValidatedGame =
    valid(new BattleshipMinesweeper(playerState.copy(
      // TODO Come here
      board = playerState.board
    )))

  def inspect(player: Int, position: Position): OceanCell =
    playerState.board.get(position) match {
      case Some(Right(ShipInPlay(_,parts))) => parts(position)
      case _=> OceanCell.water
    }
}
