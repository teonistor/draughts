package io.github.teonistor.bsms.core.experimental

import io.github.teonistor.bsms.comm.{AsciiDisplayablePlayerState, Cursor}
import io.github.teonistor.bsms.core._
import io.github.teonistor.bsms.data.Orientation.{horizontal, vertical}
import io.github.teonistor.bsms.data.{Movement, Player, Position}
import io.github.teonistor.bsms.rule.ShipPlacementRule
import io.vavr.control.Validation.valid

case class KeyboardesqueIoState(cursor: Position,
                                movement: Movement,
                                flag: Boolean,
                                prevCur: Option[Position]) {

  private lazy val orientation = if (flag) horizontal else vertical

  def move(movement: Movement): KeyboardesqueIoState =
    copy(cursor = movement.move(cursor), movement = movement)

  def toggle(): KeyboardesqueIoState =
    copy(flag = !flag)

  def preview(playerState: PlayerState): AsciiDisplayablePlayerState.Validated =
    Some(playerState)
      .filter(!_.isStageOver)
      .map[AsciiDisplayablePlayerState.Validated] {
        case state: PlayerStateShipPlacement =>
          ShipPlacementRule.placeShip(Map.empty, state.shipsToPlace.head, cursor, orientation)
            .map(Some(_))
            .map(AsciiDisplayablePlayerState(state, Cursor.none, _))

        case state: PlayerStateMovement => prevCur match {
          // We know as a postcondition of apply() that a ship exists at pc if we get here
          case Some(pc) =>
            val (overlay, base) = ShipPlacementRule.isolateShip(state.board, pc)
            ShipPlacementRule.moveShip(overlay, pc, movement)
              .map(Some(_))
              .map(AsciiDisplayablePlayerState(state.copy(board = base), Cursor.none, _))

          case None => valid(AsciiDisplayablePlayerState(state, Cursor.own(cursor)))
        }
        case state => valid(AsciiDisplayablePlayerState(state, Cursor.opponent(cursor)))
      }
      .getOrElse(valid(AsciiDisplayablePlayerState(playerState, Cursor.none)))

  def apply(game:BattleshipMinesweeper, player: Player):(ValidatedGame, KeyboardesqueIoState)={
    (player match {
      case Player.alice => game.aliceState
      case Player.bob => game.bobState
    }) match {

      case state: PlayerStateShipPlacement => (game.placeShip(player, state.shipsToPlace.head, cursor, orientation), this)
      case _    : PlayerStateMinePlacement => (game.placeMine(player, cursor), this)
      case state: PlayerStateMovement =>
        // TODO MAYBE utility method hasShipAt in PlyerState?
        if (prevCur.isEmpty && state.board.get(cursor).flatMap(_.toOption).isDefined)
          (valid(game), copy(prevCur = Some(cursor), cursor = Vector(0,0)))
        else if(prevCur.isDefined)
          (game.moveShip(player, prevCur.get, movement), copy(prevCur = None, cursor = Vector(0,0)))
        else
          (valid(game.pass(player)), this)
      case _: PlayerStateShooting => (game.shoot(player, cursor), this)
    }
  }
}

object KeyboardesqueIoState {
  lazy val nil = new KeyboardesqueIoState(Vector(0, 0), Movement.down, false, None)
}
