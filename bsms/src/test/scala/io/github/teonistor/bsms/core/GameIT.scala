package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.comm.{AsciiArtIO, KeyboardIoGameContainer}
import io.github.teonistor.bsms.core.experimental.KeyboardesqueIoState
import io.github.teonistor.bsms.data.{GameCondition, GameSettings, ShipDescription}

import java.util.function.{Function => JuFunc}

object GameIT /*extends AnyFunSuite */{

//  test("base") {
  def main(arg: Array[String]): Unit = {

//    val settings = GameSettings(Set(
//      ShipDescription("Fishing boat", 2),
//      ShipDescription("Bomber", 3)),
//      2, 8, 8)
    val settings = GameSettings(Set(
      ShipDescription("Dingy", 1)),
      2, 8, 8)

    var game = KeyboardIoGameContainer(
      BattleshipMinesweeper(settings,
        PlayerStateShipPlacement(Map.empty, Map.empty, settings.shipsToPlace),
        PlayerStateShipPlacement(Map.empty, Map.empty, settings.shipsToPlace)),
//      BattleshipMinesweeper(settings,
//        PlayerStateMinePlacement(Map.empty, Map.empty, settings.minesToPlace),
//        PlayerStateMinePlacement(Map.empty, Map.empty, settings.minesToPlace)),
      KeyboardesqueIoState.nil,
      KeyboardesqueIoState.nil)

    def handleGameOver(): Unit = {
      window.window.dispose()
      println("Game over. " + (game.game.condition match {
        case GameCondition.aliceWins => "Alice wins."
        case GameCondition.bobWins => "Bob wins."
        case GameCondition.everyoneLoses => "Everyone loses."
        case GameCondition.stalemate => "Stalemate."
        case _=> ""
      }))
    }

    lazy val window: AsciiArtIO = KeyboardAsciiArtIoRunner.launch(() => game, _.fold(
      println(_),
      newGame => {
        val printAssignCheck: JuFunc[String,Unit] = str => {
          println(str)
          game = newGame
          if (game.game.isGameOver)
            handleGameOver()
        }

        newGame.illustration.fold(printAssignCheck, printAssignCheck)
      }))

//    def zzzzz(updatedGame: KeyboardIoGameContainer): function.Function[String, Unit] = string => {
//      println(string)
//      game = updatedGame
//    }
//
//    KeyboardAsciiArtIoRunner.launch(() => game, _.fold(
//      println(_),
//      updatedGame => {
//        val printAndAssign = zzzzz(updatedGame)
//        updatedGame.illustration.fold(printAndAssign, printAndAssign)
//      }))

    game.illustration.forEach(println)
    window.toString  // To cause the lazy to be evaluated
  }
}
