package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{GameState, Position, Settings}
import io.github.teonistor.draughts.rule.{AvailableMovesRule, GameOverChecker, PromotionRule}
import org.scalatest.funsuite.AnyFunSuite

import java.io.{BufferedReader, InputStreamReader}

object EvolvingIT extends AnyFunSuite{

  def main(arg: Array[String]): Unit = {
    val inputLine = " *(-?\\d+) +(-?\\d+) +(-?\\d+) +(-?\\d+) *".r
    val terminalView = new TerminalView()
    var game = new Game(new AvailableMovesRule(),
      new PromotionRule(),
      new GameOverChecker(),
      Settings(4,5,2),
      GameState(Map(
        Position(0,0) -> Piece.whitePeon,
        Position(2,0) -> Piece.whitePeon,
        Position(1,1) -> Piece.whitePeon,
        Position(3,3) -> Piece.blackPeon,
        Position(1,3) -> Piece.blackPeon,
        Position(2,2) -> Piece.blackPeon),
        Player.white))

    val rd = new BufferedReader(new InputStreamReader(System.in))
    println(terminalView.display(game))

    while(true) {
      val mtch = inputLine.pattern.matcher(rd.readLine())
      if (mtch.matches()) {
        val result = game.move(
          Position(mtch.group(1).toInt, mtch.group(2).toInt),
          Position(mtch.group(3).toInt, mtch.group(4).toInt))
        if (result.isValid) {
          game = result.get
          println(terminalView.display(game))
        }
        else println(result.getError)

      } else
        println("Busted")
    }
  }
}
