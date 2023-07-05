package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.Settings
import io.vavr.control.Validation.{invalid, valid}
import org.mockito.Mockito.verify
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike
import org.springframework.test.util.ReflectionTestUtils.{getField, setField}

class JunctureTest extends AnyFunSuiteLike with IdiomaticMockito {

  test("start new game") {
    val settings = mock[Settings]
    val game = mock[Game]

    val juncture = new Juncture(actualInput => {
      assert(actualInput == settings)
      game
    }, null)
    juncture.start(settings)

    assert(getField(juncture, "game") == game)
  }

  test("progress when null") {
    val view = mock[View]
    new Juncture(null, view).progress(null)

    verify(view).announce("No game in progress")
  }

  test("progress when valid") {
    val view = mock[View]
    val expectedInput = mock[Game]
    val expectedOutput = mock[Game]

    val juncture = new Juncture(null, view)
    setField(juncture, "game", expectedInput)

    juncture.progress(actualInput => {
      assert(actualInput == expectedInput)
      valid(expectedOutput)
    })

    assert(getField(juncture, "game") == expectedOutput)
    verify(view).display(expectedOutput)
  }

  test("progress when invalid") {
    val view = mock[View]
    val unchanged = mock[Game]

    val juncture = new Juncture(null, view)
    setField(juncture, "game", unchanged)

    juncture.progress(actualInput => {
      assert(actualInput == unchanged)
      invalid("Busted!")
    })

    assert(getField(juncture, "game") == unchanged)
    verify(view).announce("Busted!")
  }
}
