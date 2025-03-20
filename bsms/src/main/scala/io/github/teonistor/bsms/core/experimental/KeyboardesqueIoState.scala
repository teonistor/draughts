package io.github.teonistor.bsms.core.experimental

import io.github.teonistor.bsms.core._
import io.github.teonistor.bsms.data.Orientation.{horizontal, vertical}
import io.github.teonistor.bsms.data.{Movement, Player, Position}
import io.vavr.control.Validation.valid

case class KeyboardesqueIoState(cursor: Position,
                                flag: Boolean,
                                prevCur: Option[Position]) {

  private lazy val orientation = if (flag) horizontal else vertical

  def move(movement: Movement): KeyboardesqueIoState =
    copy(cursor = movement.move(cursor))

  def toggle(): KeyboardesqueIoState =
    copy(flag = !flag)

  def preview(playerState: PlayerState): (ValidatedState, Option[Position]) = {
    Some(playerState)
      .filter(!_.isStageOver)
      .map[(ValidatedState,Option[Position])] {
        case state: PlayerStateShipPlacement => (state.placeShip(state.shipsToPlace.head, cursor, orientation), None)
        case state: PlayerStateMovement => prevCur.fold((valid[String,PlayerState](state), Option(cursor)))(
       // TODO Would help if the state declared its actual type as return type; tried, but variance goes boom because Validation is a Vavr (non-Scala) class
          prevCur => (state.moveShip(prevCur, cursor).map(_.asInstanceOf[PlayerStateMovement].copy(moveToMake = true)), None))
        case state => (valid(state), Some(cursor))
      }
      .getOrElse((valid(playerState), None))
  }

  def apply(game:BattleshipMinesweeper, player: Player):(ValidatedGame, KeyboardesqueIoState)={
    (player match {
      case Player.alice => game.aliceState
      case Player.bob => game.bobState
    }) match {

      case state: PlayerStateShipPlacement => (game.placeShip(player, state.shipsToPlace.head, cursor, orientation), this)
      case _    : PlayerStateMinePlacement => (game.placeMine(player, cursor), this)
      case state: PlayerStateMovement =>
        if (prevCur.isEmpty && state.board.get(cursor).flatMap(_.toOption).isDefined)
          (valid(game), copy(prevCur = Some(cursor), cursor = Vector(0,0)))
        else if(prevCur.isDefined)
          (game.moveShip(player, prevCur.get, cursor), copy(prevCur = None, cursor = Vector(0,0)))
        else
          (valid(game.pass(player)), this)
      case _: PlayerStateShooting => (game.shoot(player, cursor), this)
    }
  }
}

object KeyboardesqueIoState {
  lazy val nil = new KeyboardesqueIoState(Vector(0, 0), false, None)
}
