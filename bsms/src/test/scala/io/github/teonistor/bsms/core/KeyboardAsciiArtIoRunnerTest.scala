package io.github.teonistor.bsms.core

import io.github.teonistor.bsms.comm.{AsciiArtIO, KeyboardIoGameContainer}
import io.github.teonistor.bsms.core.KeyboardAsciiArtIoRunner.ValidatedKeyboardIoGameContainer
import io.github.teonistor.bsms.core.experimental.KeyboardesqueIoState
import io.github.teonistor.bsms.data.{Movement, Player}
import io.vavr.control.Validation.{invalid, valid}
import org.mockito.IdiomaticMockito
import org.mockito.MockedConstruction.Context
import org.mockito.Mockito.mockConstruction
import org.scalatest.BeforeAndAfter
import org.scalatest.funsuite.AnyFunSuite

import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.Using

class KeyboardAsciiArtIoRunnerTest extends AnyFunSuite with IdiomaticMockito with BeforeAndAfter {

  private val getter = mock[() => KeyboardIoGameContainer]
  private val setter = mock[ValidatedKeyboardIoGameContainer => Unit]

  private val gameIn = mock[BattleshipMinesweeper]
  private val stateIn = mock[KeyboardesqueIoState]
  private val movement = mock[Movement]
  private val stateOut = mock[KeyboardesqueIoState]
  private val gameOut = mock[BattleshipMinesweeper]

  private val (aliceMove,
               aliceToggle,
               aliceConfirm,
               bobMove,
               bobToggle,
               bobConfirm) = {
      type AsciiArtIoConstructorParams = (Movement => Unit, () => Unit, () => Unit, Movement => Unit, () => Unit, () => Unit)
      var capturedParams: AsciiArtIoConstructorParams = null
      Using(mockConstruction(classOf[AsciiArtIO], (_: AsciiArtIO, context: Context) => {
        val aliceMove :: aliceToggle :: aliceConfirm :: bobMove :: bobToggle :: bobConfirm :: Nil = context.arguments().asScala.toList
        capturedParams = (aliceMove, aliceToggle, aliceConfirm, bobMove, bobToggle, bobConfirm).asInstanceOf[AsciiArtIoConstructorParams]
      })) { _=>
        KeyboardAsciiArtIoRunner.launch(getter, setter)
        capturedParams
      }.get
    }

  test("aliceMove") {
    getter() returns KeyboardIoGameContainer(null, stateIn, null)
    stateIn.move(movement) returns stateOut

    aliceMove(movement)

    setter(valid(KeyboardIoGameContainer(null, stateOut, null))) wasCalled once
  }

  test("aliceToggle") {
    getter() returns KeyboardIoGameContainer(null, stateIn, null)
    stateIn.toggle() returns stateOut

    aliceToggle()

    setter(valid(KeyboardIoGameContainer(null, stateOut, null))) wasCalled once
  }

  test("aliceConfirm - valid") {
    getter() returns KeyboardIoGameContainer(gameIn, stateIn, null)
    stateIn.apply(gameIn, Player.alice) returns ((valid(gameOut), stateOut))

    aliceConfirm()

    setter(valid(KeyboardIoGameContainer(gameOut, stateOut, null))) wasCalled once
  }

  test("aliceConfirm - invalid") {
    getter() returns KeyboardIoGameContainer(gameIn, stateIn, null)
    stateIn.apply(gameIn, Player.alice) returns ((invalid("Boo hoo"), null))

    aliceConfirm()

    setter(invalid("Boo hoo")) wasCalled once
  }

  test("bobMove") {
    getter() returns KeyboardIoGameContainer(null, null, stateIn)
    stateIn.move(movement) returns stateOut

    bobMove(movement)

    setter(valid(KeyboardIoGameContainer(null, null, stateOut))) wasCalled once
  }

  test("bobToggle") {
    getter() returns KeyboardIoGameContainer(null, null, stateIn)
    stateIn.toggle() returns stateOut

    bobToggle()

    setter(valid(KeyboardIoGameContainer(null, null, stateOut))) wasCalled once
  }

  test("bobConfirm - valid") {
    getter() returns KeyboardIoGameContainer(gameIn, null, stateIn)
    stateIn.apply(gameIn, Player.bob) returns ((valid(gameOut), stateOut))

    bobConfirm()

    setter(valid(KeyboardIoGameContainer(gameOut, null, stateOut))) wasCalled once
  }

  test("bobConfirm - invalid") {
    getter() returns KeyboardIoGameContainer(gameIn, null, stateIn)
    stateIn.apply(gameIn, Player.bob) returns ((invalid("Boo hoo"), null))

    bobConfirm()

    setter(invalid("Boo hoo")) wasCalled once
  }

  after {
//    TODO I don't know why this doesn't work
//    verifyNoMoreInteractions(getter, setter, gameIn, stateIn, movement, stateOut, gameOut)
    reset(getter, setter, gameIn, stateIn, movement, stateOut, gameOut)
  }
}
