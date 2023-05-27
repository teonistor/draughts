package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{GameState, Position}
import io.github.teonistor.draughts.rule.{AvailableMovesRule, PromotionRule}
import org.scalatest.funsuite.AnyFunSuite

import java.io.{BufferedReader, InputStreamReader}
import java.lang.Math.{max, min}

object EvolvingIT extends AnyFunSuite{

  def main(arg: Array[String]): Unit = {
    val ll = "(-?\\d+) +(-?\\d+) +(-?\\d+) +(-?\\d+)".r
    var game = new Game(
      new AvailableMovesRule(),
      new PromotionRule(),
      null,
      GameState(Map(
        Position(0,0) -> Piece.whitePeon,
        Position(2,0) -> Piece.whitePeon,
        Position(1,1) -> Piece.whitePeon,
        Position(7,7) -> Piece.blackPeon,
        Position(5,7) -> Piece.blackPeon,
        Position(6,6) -> Piece.blackPeon),
        Player.white))

    val rd = new BufferedReader(new InputStreamReader(System.in))
    while(true) {
      val (maxY, minY, minX, maxX) = game.gameState.board.keys
        .map(p => (p.y,p.y,p.x,p.x))
        .reduce[(Int,Int,Int,Int)] {
          case((a,b,c,d),(e,f,g,h)) => (max(a,e), min(b,f), min(c,g), max(d,h))
        }
      (minY to maxY).reverse.foreach(y =>
        println((minX to maxX).map(x =>
          game.gameState.board.get(Position(x,y))
        .map {
          case Piece.blackKing => "B"
          case Piece.blackPeon => "b"
          case Piece.whiteKing => "W"
          case Piece.whitePeon => "w"
        } .getOrElse("."))
          .mkString))

      val mtch = ll.pattern.matcher(rd.readLine())
      if (mtch.matches()) {
        val result = game.move(
          Position(mtch.group(1).toInt, mtch.group(2).toInt),
          Position(mtch.group(3).toInt, mtch.group(4).toInt))
        if (result.isValid) game = result.get
        else println(result.getError)
      }
    }
  }
}
