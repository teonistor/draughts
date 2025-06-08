package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.comm.{AsciiArtIO, KeyboardIoGameContainer}
import io.github.teonistor.bsms.data.Player.{alice, bob}
import io.vavr.control.Validation
import io.vavr.control.Validation.valid

object KeyboardAsciiArtIoRunner {
  type ValidatedKeyboardIoGameContainer = Validation[String, KeyboardIoGameContainer]

  def launch(getter: () => KeyboardIoGameContainer, setter: ValidatedKeyboardIoGameContainer => Unit): AsciiArtIO = {

    def v(func: KeyboardIoGameContainer => KeyboardIoGameContainer) =
      setter(valid(func(getter())))

    def w(func: KeyboardIoGameContainer => ValidatedKeyboardIoGameContainer) =
      setter(func(getter()))

    new AsciiArtIO(
      move => v(_.updateAlice(_.move(move))),
      ()   => v(_.updateAlice(_.toggle())),
      ()   => w(kigc => {
                val (result, newAliceIO) = kigc.aliceIO(kigc.game, alice)
                result.map(newGame => kigc.copy(game = newGame, aliceIO = newAliceIO))
              }),
      move => v(_.updateBob(_.move(move))),
      ()   => v(_.updateBob(_.toggle())),
      ()   => w(kigc => {
                val (result, newBobIO) = kigc.bobIO(kigc.game, bob)
                result.map(newGame => kigc.copy(game = newGame, bobIO = newBobIO))
              }))
  }
}
