package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.Position
import io.vavr.control.Validation
import org.mockito.BDDMockito.{willDoNothing, willReturn}
import org.mockito.captor.ArgCaptor
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets.UTF_8
import scala.util.Random.between

class TerminalInputTest extends AnyFunSuiteLike with IdiomaticMockito {

  test("exit") {
    new TerminalInput(new ByteArrayInputStream(lines()), mock[Juncture]).run()
  }

  test("pass") {
    val juncture = mock[Juncture]
    val gameIn = mock[Game]
    val gameOut = mock[Validation[String, Game]]
    val gameFunc = ArgCaptor[Game=>Validation[String,Game]]

    willDoNothing().given(juncture).progress(gameFunc)
    willReturn(gameOut).given(gameIn).pass()

    new TerminalInput(new ByteArrayInputStream(lines("pass")), juncture).run()

    assert(gameFunc.value(gameIn) == gameOut)
  }

  test("move") {
    val juncture = mock[Juncture]
    val gameIn = mock[Game]
    val gameOut = mock[Validation[String, Game]]
    val gameFunc = ArgCaptor[Game=>Validation[String,Game]]

    willDoNothing().given(juncture).progress(gameFunc)
    willReturn(gameOut).given(gameIn).move(Position(1,2), Position(3,4))

    new TerminalInput(new ByteArrayInputStream(lines("1 2 3 4")), juncture).run()

    assert(gameFunc.value(gameIn) == gameOut)
  }

  private def lines(lns: String*) =
    lns
      .prepended("garbage")
      .appended("exit")
      .map(" " * between(1,4) +_+ " " * between(1,4))
      .mkString("\n")
      .getBytes(UTF_8)
}
