package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.comm.AsciiArt.illustrateGame
import io.github.teonistor.bsms.core.BattleshipMinesweeper
import io.github.teonistor.bsms.core.experimental.KeyboardesqueIoState
import io.vavr.control.Validation

case class KeyboardIoGameContainer(game: BattleshipMinesweeper,
                                   aliceIO: KeyboardesqueIoState,
                                   bobIO: KeyboardesqueIoState) {

  lazy val illustration: Validation[String,String] =
    (aliceIO.preview(game.aliceState) combine bobIO.preview(game.bobState))
      .ap { case (a, b) => illustrateGame(a, b, game.settings) }
      .mapError[String](_.mkString(". "))
}
