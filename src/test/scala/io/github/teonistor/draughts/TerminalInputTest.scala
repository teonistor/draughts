package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.Position
import io.vavr.control.Validation
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.`given`
import org.mockito.Mockito.{mock, verify, verifyNoMoreInteractions}
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuiteLike
import org.apache.commons
import org.mockito.ArgumentCaptor

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8
import scala.util.Random.between

class TerminalInputTest extends AnyFunSuiteLike with BeforeAndAfterEach {

  private val juncture = mock(classOf[Juncture])
  private val gameIn = mock(classOf[Game])
  private val gameOut = mock(classOf[Validation[String,Game]])

  test("exit") {
    new TerminalInput(new ByteArrayInputStream(lines()), juncture)
      .run()
  }

  test("pass") {
    val ti = new TerminalInput(new ByteArrayInputStream(lines("pass")), juncture)
    given(gameIn.pass()) willReturn gameOut
//    val value = any()
//    val value1 = juncture.progress(value)
//    val v = given(value1)
//    v.will(invocation =>
//      assert(invocation.getArgument[Game => Validation[String,Game]](0)(gameIn) == gameOut))

    val captor: ArgumentCaptor[Game=>Validation[String,Game]] = ArgumentCaptor.forClass(classOf[Game=>Validation[String,Game]])

    ti.run()
    val juncture1 = verify(juncture)
    val function = captor.capture()
    val v=juncture1.progress(function)

    // Did I find a bug in Scala or WTAF??
    println("lalala" + v)

    assert(captor.getValue()(gameIn) == gameOut)
    verify(gameIn).pass()
  }

  test("move") {
    val ti = new TerminalInput(new ByteArrayInputStream(lines("1 2 3 4")), juncture)
    given(gameIn.move(Position(1,2), Position(3,4))) willReturn gameOut

    val captor: ArgumentCaptor[Game => Validation[String, Game]] = ArgumentCaptor.forClass(classOf[Game => Validation[String, Game]])

    ti.run()
    val juncture1 = verify(juncture)
    val function = captor.capture()
    val v = juncture1.progress(function)

    // Did I find a bug in Scala or WTAF??
    println("lalala" + v)

    assert(captor.getValue()(gameIn) == gameOut)
    verify(gameIn).move(Position(1,2), Position(3,4))
  }

  private def lines(lns: String*) =
    lns.appended("exit")
      .map(" " * between(1,4) +_+ " " * between(1,4))
      .mkString("\n")
      .getBytes(UTF_8)

  override protected def afterEach(): Unit =
    verifyNoMoreInteractions(juncture, gameIn, gameOut)
}
