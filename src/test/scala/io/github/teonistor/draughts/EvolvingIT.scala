package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{GameState, Position, Settings}
import io.github.teonistor.draughts.rule.{AvailableMovesRule, GameOverChecker, PromotionRule}
import io.vavr.control.Validation
import org.scalatest.funsuite.AnyFunSuite

import java.io.{BufferedReader, InputStreamReader}

object EvolvingIT extends AnyFunSuite{

  def main(arg: Array[String]): Unit = {
    val inputLine = "(-?\\d+) +(-?\\d+) +(-?\\d+) +(-?\\d+)".r
    val terminalView = new TerminalView()
    val initialBoardProvider = new InitialBoardProvider()

    val settings = Settings(6, 6, 2)
    var game = new Game(new AvailableMovesRule(),
      new PromotionRule(settings),
      new GameOverChecker(),
      settings,
      GameState(
        initialBoardProvider.createBoard(settings),
        Player.white,
        None))

    val rd = new BufferedReader(new InputStreamReader(System.in))
    println(terminalView.display(game))

    def assignIfValidAndDisplay(result: Validation[String, Game]): Unit = {
      if (result.isValid) {
        game = result.get
        println(terminalView.display(game))
      }
      else println(result.getError)
    }

    while (true) {
      val line = rd.readLine().strip()
      val mtch = inputLine.pattern.matcher(line)

      if (mtch.matches())
        assignIfValidAndDisplay(game.move(
          Position(mtch.group(1).toInt, mtch.group(2).toInt),
          Position(mtch.group(3).toInt, mtch.group(4).toInt)))

      else if ("pass" equalsIgnoreCase line)
        assignIfValidAndDisplay(game.pass())

      else
        println("Busted")
    }
  }
}
