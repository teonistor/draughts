package io.github.teonistor.bsms.core.experimental

import io.github.teonistor.bsms.comm.AsciiArt.illustrateGame
import io.github.teonistor.bsms.comm.AsciiArtIO
import io.github.teonistor.bsms.core.BattleshipMinesweeper
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
}

object KeyboardIoGameContainer {
  type ValidatedKeyboardIoGameContainer = Validation[String, KeyboardIoGameContainer]

  def bindToAsciiArtIO(getter: () => KeyboardIoGameContainer, setter: ValidatedKeyboardIoGameContainer => Unit): AsciiArtIO = {

    def v(func: KeyboardIoGameContainer => KeyboardIoGameContainer) =
      setter(valid(func(getter())))

    def w(func: KeyboardIoGameContainer => ValidatedKeyboardIoGameContainer) =
      setter(func(getter()))

    new AsciiArtIO(
      move => v(kigc => kigc.copy(aliceIO = kigc.aliceIO.move(move))),
        () => v(kigc => kigc.copy(aliceIO = kigc.aliceIO.toggle())),
        () => w(kigc => {
          val (result, newAliceIO) = kigc.aliceIO.apply(kigc.game, alice)
          result.map(newGame => kigc.copy(game = newGame, aliceIO = newAliceIO))
        }),
      move => v(kigc => kigc.copy(bobIO = kigc.bobIO.move(move))),
        () => v(kigc => kigc.copy(bobIO = kigc.bobIO.toggle())),
        () => w(kigc => {
          val (result, newBobIO) = kigc.bobIO.apply(kigc.game, bob)
          result.map(newGame => kigc.copy(game = newGame, bobIO = newBobIO))
        }))
  }
}
