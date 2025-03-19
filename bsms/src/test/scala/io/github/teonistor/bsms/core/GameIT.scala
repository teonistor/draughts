package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.core.experimental.{KeyboardIoGameContainer, KeyboardesqueIoState}
import io.github.teonistor.bsms.data._

object GameIT /*extends AnyFunSuite */{

//  test("base") {
  def main(arg: Array[String]): Unit = {

    val settings = GameSettings(Set(
      ShipDescription("Fishing boat", 2),
      ShipDescription("Bomber", 3)),
      2, 8, 8)

    var wg = KeyboardIoGameContainer(
      BattleshipMinesweeper(settings,
        PlayerStateShipPlacement(Map.empty, Map.empty, settings.shipsToPlace),
        PlayerStateShipPlacement(Map.empty, Map.empty, settings.shipsToPlace)),
      KeyboardesqueIoState.nil,
      KeyboardesqueIoState.nil)

    KeyboardIoGameContainer.bindToAsciiArtIO(() => wg, _.fold(
      println(_),
      nwg => nwg.illustration.fold(
        s => {
          println(s)
          wg = nwg
        },
        s => {
          println(s)
          wg = nwg
        })))

    wg.illustration.forEach(println)
  }

  implicit class IntVectorAddition(private val l: Position) extends AnyVal {
    def +(r: Vector[Int]): Vector[Int] =
      Vector.tabulate(l.length)(i => l(i) + r.lift(i).getOrElse(0))
  }
}
