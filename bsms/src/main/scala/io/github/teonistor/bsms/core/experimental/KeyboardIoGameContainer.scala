package io.github.teonistor.bsms.core.experimental

import io.github.teonistor.bsms.comm.AsciiArt.illustrateGame
import io.github.teonistor.bsms.comm.AsciiArtIO
import io.github.teonistor.bsms.core.BattleshipMinesweeper
import io.github.teonistor.bsms.core.experimental.KeyboardIoGameContainer.ValidatedKeyboardIoGameContainer
import io.github.teonistor.bsms.data.Player._
import io.vavr.control.Validation
import io.vavr.control.Validation.valid

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

  def bind(assigner: ValidatedKeyboardIoGameContainer => Unit) =
    new AsciiArtIO(
      move => assigner(valid(copy(aliceIO = aliceIO.move(move)))),
      () => assigner(valid(copy(aliceIO = aliceIO.toggle()))),
      () => assigner({
        val (result, newAliceIO) = aliceIO.apply(game, alice)
        result.map(newGame => copy(game = newGame, aliceIO = newAliceIO))
      }),
      move => assigner(valid(copy(bobIO = bobIO.move(move)))),
      () => assigner(valid(copy(bobIO = bobIO.toggle()))),
      () => assigner({
        val (result, newBobIO) = bobIO.apply(game, bob)
        result.map(newGame => copy(game = newGame, bobIO = newBobIO))
      }))
}

object KeyboardIoGameContainer {
  type ValidatedKeyboardIoGameContainer = Validation[String, KeyboardIoGameContainer]
}
