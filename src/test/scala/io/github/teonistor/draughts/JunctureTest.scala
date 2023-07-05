package io.github.teonistor.draughts

import io.vavr.control.Validation.{invalid, valid}
import org.mockito.Mockito.verify
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike
import org.springframework.test.util.ReflectionTestUtils

class JunctureTest extends AnyFunSuiteLike with IdiomaticMockito {


  test("progress when null") {
    val view = mock[View]
    new Juncture(view).progress(null)

    verify(view).announce("No game in progress")
  }

  test("progress when valid") {
    val view = mock[View]
    val expectedInput = mock[Game]
    val expectedOutput = mock[Game]

    val juncture = new Juncture(view)
    ReflectionTestUtils.setField(juncture, "game", expectedInput)

    juncture.progress(actualInput => {
      assert(actualInput == expectedInput)
      valid(expectedOutput)
    })

    assert(ReflectionTestUtils.getField(juncture, "game") == expectedOutput)
    verify(view).display(expectedOutput)
  }

  test("progress when invalid") {
    val view = mock[View]
    val unchanged = mock[Game]

    val juncture = new Juncture(view)
    ReflectionTestUtils.setField(juncture, "game", unchanged)

    juncture.progress(actualInput => {
      assert(actualInput == unchanged)
      invalid("Busted!")
    })

    assert(ReflectionTestUtils.getField(juncture, "game") == unchanged)
    verify(view).announce("Busted!")
  }
}
