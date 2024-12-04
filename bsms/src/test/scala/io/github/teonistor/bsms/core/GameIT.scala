package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.comm.AsciiArt.illustrateGame
import io.github.teonistor.bsms.comm.{AAI, AsciiArtIO}
import io.github.teonistor.bsms.data.Orientation.{horizontal, vertical}
import io.github.teonistor.bsms.data.Player.{alice, bob}
import io.github.teonistor.bsms.data._
import io.vavr.control.Validation
import io.vavr.control.Validation.valid

object GameIT /*extends AnyFunSuite */{

  case class IoState(aliceCursor:      Position,
                     bobCursor:        Position,
                     aliceOrientation: Orientation,
                     bobOrientation:   Orientation)

  case class NioState(cursor: Position,
                      flag: Boolean,
                      prevCur: Option[Position]) {

    private lazy val orientation = if (flag) horizontal else vertical

    def move(movement: Vector[Int]): NioState =
      copy(cursor = cursor + movement)

    def toggle(): NioState =
      copy(flag = !flag)

    def preview(playerState: PlayerState): (Validation[String, PlayerState], Option[Position]) = {
      Some(playerState)
        .filter(!_.isStageOver)
        .map[(Validation[String,PlayerState],Option[Position])] {
          case state: PlayerStateShipPlacement => (state.placeShip(state.shipsToPlace.head, cursor, orientation), None)
          case state: PlayerStateMinePlacement => (valid(state), Some(cursor))
       // TODO Quite terrible hacks here... we probably need preview...() integrated into the state
          case state: PlayerStateMovement => prevCur.fold((valid[String,PlayerState](state), Option(cursor)))(
                                               prevCur => (state.moveShip(prevCur, cursor).map(_.asInstanceOf[PlayerStateMovement].copy(moveToMake = true)), None))
          case state: PlayerStateShooting => (valid(state), Some(cursor))
        }
        .getOrElse((valid(playerState), None))
    }

    def effect(game:BattleshipMinesweeper, player: Player):(ValidatedGame, NioState)={
      val state = player match {
        case Player.alice => game.aliceState
        case Player.bob => game.bobState
      }

      state match {
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

//  test("base") {
def main(arg: Array[String]): Unit = {

    val settings = GameSettings(Set(
      ShipDescription("Fishing boat", 2),
      ShipDescription("Bomber", 3)),
      2, 8, 8)
    var game = BattleshipMinesweeper(settings, PlayerStateShipPlacement(Map.empty, Map.empty, settings.shipsToPlace), PlayerStateShipPlacement(Map.empty, Map.empty, settings.shipsToPlace))
//    var ioState = IoState(Vector(0,0), Vector(0,0), horizontal, horizontal)

  var aliceIo = NioState(Vector(0, 0), false, None)
  var bobIo = NioState(Vector(0, 0), false, None)

//    def mkAlice() = {
//      Some(game.aliceState).filter(_.isStageOver)
//        .orElse(Option(game.aliceState.placeShip(game.aliceState.asInstanceOf[PlayerStateShipPlacement].shipsToPlace.head, ioState.aliceCursor, ioState.aliceOrientation).getOrNull()))
//    }
//
//    def mkBob() = {
//      Some(game.bobState).filter(_.isStageOver)
//        .orElse(Option(game.bobState.placeShip(game.bobState.asInstanceOf[PlayerStateShipPlacement].shipsToPlace.head, ioState.bobCursor, ioState.bobOrientation).getOrNull()))
//    }

    def display(): Unit = {
      val (alicePreview, aliceCursor) = aliceIo.preview(game.aliceState)
      val (bobPreview, bobCursor) = bobIo.preview(game.bobState)
      println(illustrateGame(game.copy(aliceState = alicePreview.getOrElse(game.aliceState),
                                       bobState = bobPreview.getOrElse(game.bobState)),
              aliceCursor, bobCursor))
    }
    display()

    new AsciiArtIO(new AAI {

      override def aliceMove(move: Vector[Int]): Unit = {
        aliceIo = aliceIo.move(move)
        display()
      }

      override def aliceToggle(): Unit = {
        aliceIo = aliceIo.toggle()
        display()
      }

      override def aliceConfirm(): Unit = {
        val (result, newIo) = aliceIo.effect(game, alice)
        result.fold(err => println(err), { newGame =>
          aliceIo = newIo
          game = newGame
        })
        display()
      }

      override def bobMove(move: Vector[Int]): Unit = {
        bobIo = bobIo.move(move)
        display()
      }

      override def bobToggle(): Unit = {
        bobIo = bobIo.toggle()
        display()
      }

      override def bobConfirm(): Unit = {
        val (result, newIo) = bobIo.effect(game, bob)
        result.fold(err => println(err), { newGame =>
          bobIo = newIo
          game = newGame
        })
        display()
      }
    })
}
  implicit class IntVectorAddition(private val l: Position) extends AnyVal {
    def +(r: Vector[Int]): Vector[Int] =
      Vector.tabulate(l.length)(i => l(i) + r.lift(i).getOrElse(0))
  }
}
