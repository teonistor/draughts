package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.data._

import java.util.function.{Function => JuFunction}

class BattleshipMinesweeper(val aliceState: PlayerState,
                            val bobState: PlayerState) {

  def placeShip(player: Player, ship: ShipDescription, position: Position, orientation: Orientation): ValidatedGame =
    act(player, (state, update) =>
      state.placeShip(ship, position, orientation)
        .map(update))

  def moveShip(player: Player, position: Position, movement: Vector[Int]): ValidatedGame =
    act(player, (state, update) =>
      state.moveShip(position, movement)
        .map(update))

  def useShip(player: Player, ship: ShipDescription): ValidatedGame =
    act(player, (state, update) =>
      state.useShip(ship)
        .map(update))

  def useMine(player: Player): ValidatedGame =
    act(player, (state, update) =>
      state.useMine()
        .map(update))

  private def act[T](player: Player, action: (PlayerState, JuFunction[PlayerState, BattleshipMinesweeper]) => T) =
    player match {
      case Player.alice => action(aliceState, new BattleshipMinesweeper(_, bobState))
      case Player.bob => action(bobState, new BattleshipMinesweeper(aliceState, _))
    }
}
