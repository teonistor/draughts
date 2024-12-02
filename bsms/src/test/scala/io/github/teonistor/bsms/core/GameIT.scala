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
                      flag: Boolean) {

    def move(movement:Vector[Int]) =
      copy(cursor = cursor + movement)

    def toggle() =
      copy(flag = !flag)

    def resolve(playerState: PlayerState):Validation[String,PlayerState] = {
      Some(playerState)
        .filter(!_.isStageOver)
        .map {
          case state: PlayerStateShipPlacement => state.placeShip(state.shipsToPlace.head, cursor, if (flag) horizontal else vertical)
          case state: PlayerStateMinePlacement => valid[String,PlayerState](state.placeMine(cursor))
//        case state: PlayerStateMovement
//        case state: PlayerStateShooting
      }
        .getOrElse(valid(playerState))
    }
  }

//  test("base") {
def main(arg: Array[String]): Unit = {

    val settings = GameSettings(Set(
      ShipDescription("Fishing boat", 2),
      ShipDescription("Bomber", 3)),
      2, 8, 8)
    var game = BattleshipMinesweeper(settings, PlayerStateShipPlacement(Map.empty, Map.empty, settings.shipsToPlace), PlayerStateShipPlacement(Map.empty, Map.empty, settings.shipsToPlace))
    var ioState = IoState(Vector(0,0), Vector(0,0), horizontal, horizontal)

  var aliceIo = new NioState(Vector(0,0), false)
  var bobIo = new NioState(Vector(0,0), false)

    def mkAlice() = {
      Some(game.aliceState).filter(_.isStageOver)
        .orElse(Option(game.aliceState.placeShip(game.aliceState.asInstanceOf[PlayerStateShipPlacement].shipsToPlace.head, ioState.aliceCursor, ioState.aliceOrientation).getOrNull()))
    }

    def mkBob() = {
      Some(game.bobState).filter(_.isStageOver)
        .orElse(Option(game.bobState.placeShip(game.bobState.asInstanceOf[PlayerStateShipPlacement].shipsToPlace.head, ioState.bobCursor, ioState.bobOrientation).getOrNull()))
    }

    def display(): Unit =
      println(illustrateGame(game.copy(aliceState = mkAlice().getOrElse(game.aliceState), bobState = mkBob().getOrElse(game.bobState))))

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
        game.placeShip(alice, game.aliceState.asInstanceOf[PlayerStateShipPlacement].shipsToPlace.head, ioState.aliceCursor, ioState.aliceOrientation)
          .fold(err => println(err),
                 ng => game=ng)
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
        game.placeShip(bob, game.bobState.asInstanceOf[PlayerStateShipPlacement].shipsToPlace.head, ioState.bobCursor, ioState.bobOrientation)
          .fold(err => println(err),
                 ng => game=ng)
        display()
      }
    })

//    Thread.sleep(100000)

//    display(game)
//    Thread.sleep(1000)
//    game = game.placeShip(alice, ShipDescription("Fishing boat", 2), Vector(4, 1), horizontal).get()
//    display(game)
//    Thread.sleep(1000)
//    game = game.placeShip(bob, ShipDescription("Bomber", 3), Vector(3, 3), horizontal).get()
//    display(game)
//    Thread.sleep(1000)
//    game = game.placeShip(bob, ShipDescription("Fishing boat", 2), Vector(4, 1), vertical).get()
//    display(game)
//    Thread.sleep(1000)
//    game = game.placeShip(alice, ShipDescription("Bomber", 3), Vector(2, 2), horizontal).get()
//    display(game)
//    Thread.sleep(1000)
//    game = game.placeMine(alice, Vector(5, 6)).get
//    display(game)
//    Thread.sleep(1000)
//    game = game.placeMine(bob, Vector(3 ,2)).get
//    display(game)
  }

//  private def display(game: BattleshipMinesweeper) = {
//    println(illustrateGame(game))
////    println("Alice:")
////    println(displayOne(game.aliceState.board))
////    println("Bob:")
////    println(displayOne(game.bobState.board))
////    println("----------------------------------------")
//  }

  private def displayOne(board: OwnBoard) =
    (0 to 9).map(y =>
      (0 to 9).map(x => Vector(x, y))
        .map(pos => board.get(pos) map {
          case Right(ShipInPlay(_, parts)) => parts(pos)
          case Left(cell) => cell
        })
        .map {
          case Some(OceanCell.mine) => "*"
          case Some(OceanCell.healthyShip) => "O"
          case Some(OceanCell.damagedShip) => "#"
          case _=> " "
        }
        .mkString)
      .mkString("\n")
//}
//
// object GameIT {

  implicit class IntVectorAddition(private val l: Position) extends AnyVal {
    def +(r: Vector[Int]): Vector[Int] =
      Vector.tabulate(l.length)(i => l(i) + r.lift(i).getOrElse(0))
  }
}
