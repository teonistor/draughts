package io.github.teonistor.draughts

import io.vavr.control.Validation.{invalid, valid}
import org.mockito.scalatest.MockitoSugar
import org.scalatest.funsuite.AnyFunSuite
import org.springframework.test.util.ReflectionTestUtils

class JunctureTest extends AnyFunSuite with MockitoSugar {

  test("when valid") {
    val expectedInput = mock[Game]
    val expectedOutput = mock[Game]

    val juncture = new Juncture()
    ReflectionTestUtils.setField(juncture, "game", expectedInput)

    juncture.progress(actualInput => {
      assert(actualInput == expectedInput)
      valid(expectedOutput)
    })

    assert(ReflectionTestUtils.getField(juncture, "game") == expectedOutput)
  }

  test("when invalid") {
    val unchanged = mock[Game]

    val juncture = new Juncture()
    ReflectionTestUtils.setField(juncture, "game", unchanged)

    juncture.progress(actualInput => {
      assert(actualInput == unchanged)
      invalid("Busted!")
    })

    assert(ReflectionTestUtils.getField(juncture, "game") == unchanged)
  }
}
