package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.comm.AsciiArt.illustrateGame
import io.github.teonistor.bsms.core.BattleshipMinesweeper
import io.github.teonistor.bsms.core.experimental.KeyboardesqueIoState

case class KeyboardIoGameContainer(game: BattleshipMinesweeper,
                                   aliceIO: KeyboardesqueIoState,
                                   bobIO: KeyboardesqueIoState) {
  lazy val illustration = {
    val (alicePreview, aliceCursor) = aliceIO.preview(game.aliceState)
    val (bobPreview, bobCursor) = bobIO.preview(game.bobState)
    alicePreview.combine(bobPreview)
      .ap((a, b) => game.copy(aliceState = a, bobState = b))
      .mapError[String](_.mkString(". "))
      .map[String](illustrateGame(_, aliceCursor, bobCursor))
  }
}
