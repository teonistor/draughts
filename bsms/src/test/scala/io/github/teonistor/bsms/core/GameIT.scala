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
                      prevCur: Position) {

    private lazy val orientation = if (flag) horizontal else vertical

    def move(movement: Vector[Int]): NioState =
      copy(cursor = cursor + movement)

    def toggle(): NioState =
      copy(flag = !flag)

    def preview(playerState: PlayerState): Validation[String, PlayerState] = {
      Some(playerState)
        .filter(!_.isStageOver)
        .map[Validation[String, PlayerState]] {
          case state: PlayerStateShipPlacement => state.placeShip(state.shipsToPlace.head, cursor, orientation)
          case state: PlayerStateMinePlacement => valid(state.placeMine(cursor))
       // TODO Quite terrible hacks here... we probably need preview...() integrated into the state
          case state: PlayerStateMovement => state.moveShip(prevCur, cursor).map(_.asInstanceOf[PlayerStateMovement].copy(moveToMake = true))
          case state: PlayerStateShooting => state.shoot().map(_.asInstanceOf[PlayerStateShooting].copy(shotToShoot = true))
        }
        .getOrElse(valid(playerState))
    }

    def effect(game:BattleshipMinesweeper, player: Player):(ValidatedGame, NioState)={
      val state = player match {
        case Player.alice => game.aliceState
        case Player.bob => game.bobState
      }

      state match {
        case state: PlayerStateShipPlacement => (game.placeShip(player, state.shipsToPlace.head, cursor, orientation), this)
        case _    : PlayerStateMinePlacement => (game.placeMine(player, cursor), this)
        case state: PlayerStateMovement => (state.board.get(cursor).flatMap(_.toOption), prevCur) match {
          case (Some(_), Vector(0,0)) => (valid(game), copy(prevCur = cursor, cursor = Vector(0,0)))
          case (Some(_), _) => (game.moveShip(player, prevCur, cursor), copy(prevCur = Vector(0,0), cursor = Vector(0,0)))
          case (None   , _) => (valid(game.pass(player)), this)
        }
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

  var aliceIo = NioState(Vector(0, 0), false, Vector(0,0))
  var bobIo = NioState(Vector(0, 0), false, Vector(0,0))

//    def mkAlice() = {
//      Some(game.aliceState).filter(_.isStageOver)
//        .orElse(Option(game.aliceState.placeShip(game.aliceState.asInstanceOf[PlayerStateShipPlacement].shipsToPlace.head, ioState.aliceCursor, ioState.aliceOrientation).getOrNull()))
//    }
//
//    def mkBob() = {
//      Some(game.bobState).filter(_.isStageOver)
//        .orElse(Option(game.bobState.placeShip(game.bobState.asInstanceOf[PlayerStateShipPlacement].shipsToPlace.head, ioState.bobCursor, ioState.bobOrientation).getOrNull()))
//    }

    def display(): Unit =
      println(illustrateGame(game.copy(aliceState = aliceIo.preview(game.aliceState).getOrElse(game.aliceState),
                                       bobState   = bobIo.preview(game.bobState).getOrElse(game.bobState))))
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
