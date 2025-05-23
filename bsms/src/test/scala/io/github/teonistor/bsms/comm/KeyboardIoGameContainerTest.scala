package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.core.experimental.KeyboardesqueIoState
import io.github.teonistor.bsms.core.{BattleshipMinesweeper, PlayerState}
import io.vavr.control.Validation.{invalid, valid}
import org.mockito.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuite

class KeyboardIoGameContainerTest extends AnyFunSuite with IdiomaticMockito {

  private val aliceIO = mock[KeyboardesqueIoState]
  private val bobIO = mock[KeyboardesqueIoState]
  private val bobStateIn = mock[PlayerState]
  private val aliceStateIn = mock[PlayerState]
  private val aliceStateOut = mock[PlayerState]
  private val bobStateOut = mock[PlayerState]
  private val aliceCursor = mock[Cursor]
  private val bobCursor = mock[Cursor]

  test("everything valid") {
    withObjectMocked[AsciiArt.type] {

      val game = BattleshipMinesweeper(null, aliceStateIn, bobStateIn)
      aliceIO.preview(aliceStateIn) returns valid(AsciiDisplayablePlayerState(aliceStateOut, aliceCursor))
      bobIO.preview(bobStateIn) returns valid(AsciiDisplayablePlayerState(bobStateOut, bobCursor))
      AsciiArt.illustrateGame(BattleshipMinesweeper(null, aliceStateOut, bobStateOut), aliceCursor, bobCursor) returns "This is the game displayed, trust me"

      assert(KeyboardIoGameContainer(game, aliceIO, bobIO).illustration contains "This is the game displayed, trust me")
    }
  }

  test("invalid Alice preview") {
    val game = BattleshipMinesweeper(null, aliceStateIn, bobStateIn)
    aliceIO.preview(aliceStateIn) returns invalid("Alice had a headache")
    bobIO.preview(bobStateIn) returns valid(AsciiDisplayablePlayerState(bobStateOut, Cursor.none))

    assert(KeyboardIoGameContainer(game, aliceIO, bobIO).illustration.getError == "Alice had a headache")
  }

  test("invalid Bob preview") {
    val game = BattleshipMinesweeper(null, aliceStateIn, bobStateIn)
    aliceIO.preview(aliceStateIn) returns valid(AsciiDisplayablePlayerState(aliceStateOut, Cursor.none))
    bobIO.preview(bobStateIn) returns invalid("Bob was stuck in traffic")

    assert(KeyboardIoGameContainer(game, aliceIO, bobIO).illustration.getError == "Bob was stuck in traffic")
  }

  test("both previews invalid") {
    val game = BattleshipMinesweeper(null, aliceStateIn, bobStateIn)
    aliceIO.preview(aliceStateIn) returns invalid("Alice had a headache")
    bobIO.preview(bobStateIn) returns invalid("Bob was stuck in traffic")

    assert(KeyboardIoGameContainer(game, aliceIO, bobIO).illustration.getError == "Alice had a headache. Bob was stuck in traffic")
  }
}
