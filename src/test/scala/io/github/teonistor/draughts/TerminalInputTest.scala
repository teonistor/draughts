package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.Settings
import io.vavr.control.Validation
import org.mockito.BDDMockito.{willDoNothing, willReturn, willThrow}
import org.mockito.captor.ArgCaptor
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets.UTF_8
import scala.util.Random.between

class TerminalInputTest extends AnyFunSuiteLike with IdiomaticMockito {

  test("stray exception propagates out") {
    val juncture = mock[Juncture]
    willThrow(classOf[NoSuchElementException]).given(juncture).start(Settings(1, 3, 3))

    assertThrows[NoSuchElementException](new TerminalInput(new ByteArrayInputStream(lines("new game", "1", "3 3")), juncture).run())
  }

  test("exit") {
    new TerminalInput(new ByteArrayInputStream(lines()), mock[Juncture]).run()
  }

  test("new game") {
    val juncture = mock[Juncture]

    willDoNothing().given(juncture).start(Settings(3, 5, 6, 7))

    new TerminalInput(new ByteArrayInputStream(lines("new game", "3", "5 6  7")), juncture).run()
  }

  test("new game cannot") {
    new TerminalInput(new ByteArrayInputStream(lines("new game", "", "3 4")), mock[Juncture]).run()
  }

  test("new game with defaults") {
    val juncture = mock[Juncture]

    willDoNothing().given(juncture).start(Settings(2, 8, 8))

    new TerminalInput(new ByteArrayInputStream(lines("new game", " ", "")), juncture).run()
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

  LazyList(("1 2 3 4",        Vector(1, 2),          Vector(3, 4)),
      ("1 2 3 4 5 6",         Vector(1, 2, 3),       Vector(4, 5, 6)),
      ("1 2 3 4 5 6 7 8 9 0", Vector(1, 2, 3, 4, 5), Vector(6, 7, 8, 9, 0)))
    .foreach { case (fromTo, from, to) => test("move " + fromTo) {
      val juncture = mock[Juncture]
      val gameIn = mock[Game]
      val gameOut = mock[Validation[String, Game]]
      val gameFunc = ArgCaptor[Game => Validation[String, Game]]

      willDoNothing().given(juncture).progress(gameFunc)
      willReturn(gameOut).given(gameIn).move(from, to)

      new TerminalInput(new ByteArrayInputStream(lines(fromTo)), juncture).run()

      assert(gameFunc.value(gameIn) == gameOut)
    }}

  private def lines(lns: String*) =
    lns
      .prepended("garbage")  // Implicitly test that garbage input is ignored
      .appended("exit")
      .map(" " * between(1,4) +_+ " " * between(1,4))  // Implicitly test that leading and trailing white space is trimmed
      .mkString("\n")
      .getBytes(UTF_8)
}
