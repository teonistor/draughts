package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.data._

import java.util.function.{Function => JuFunction}

class BattleshipMinesweeper(val aliceState: PlayerState,
                            val bobState: PlayerState) {

  def placeShip(player: Player, ship: ShipDescription, position: Position, orientation: Orientation): ValidatedGame =
    act(player, (state, update) => state
      .placeShip(ship, position, orientation)
      .flatMap(_.useShip(ship))
      .map(update))

  def moveShip(player: Player, position: Position, movement: Vector[Int]): ValidatedGame =
    act(player, (state, update) =>
      state.moveShip(position, movement)
        .map(update))

  /**
   * @param player   The player performing the action (whose mine counter must be positive and will decrement)
   * @param position The position on the OTHER player's board where the mine will be placed
   */
  def placeMine(player: Player, position: Position): ValidatedGame =
    act(player, (state, update) => state
      .useMine()
      .map(update)).map(_
      .act(player.other, (otherState, otherUpdate) =>
        otherUpdate(otherState.placeMine(position))))

  private def act[T](player: Player, action: (PlayerState, JuFunction[PlayerState, BattleshipMinesweeper]) => T) =
    player match {
      case Player.alice => action(aliceState, new BattleshipMinesweeper(_, bobState))
      case Player.bob => action(bobState, new BattleshipMinesweeper(aliceState, _))
    }
}
