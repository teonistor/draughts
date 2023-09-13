package io.github.teonistor.commongaming

import io.vavr.control.Validation.{invalid, valid}
import org.mockito.BDDMockito.`given`
import org.mockito.Mockito.verify
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike
import org.springframework.test.util.ReflectionTestUtils.setField

class GamesHolderTest extends IdiomaticMockito with AnyFunSuiteLike {

  test("Create empty") {
    val holder = new GamesHolder[Nothing, Nothing](null, null)

    assert(holder.games.isEmpty)
  }

  test("New game") {
    val gameFactory = mock[String => String]
    given(gameFactory("abcd")).willReturn("My game")

    val holder = new GamesHolder[String,String](gameFactory, null)
    holder.newGame("abcd")

    assert(holder.games.values.toList == List("My game"))
  }

  test("Progress when valid") {
    val view = mock[HyperView[Object]]
    val expectedInput = mock[Object]
    val expectedOutput = mock[Object]

    val holder = new GamesHolder[Object, Object](null, view)
    setField(holder, "_games", Map("1234" -> expectedInput))

    holder.progress("1234", actualInput => {
      assert(actualInput == expectedInput)
      valid(expectedOutput)
    })

    assert(holder.games("1234") == expectedOutput)
    verify(view).display("1234", expectedOutput)
  }

  test("Progress when invalid") {
    val view = mock[HyperView[Object]]
    val unchanged = mock[Object]

    val holder = new GamesHolder[Object, Object](null, view)
    setField(holder, "_games", Map("1234" -> unchanged))

    holder.progress("1234", actualInput => {
      assert(actualInput == unchanged)
      invalid("Busted!")
    })

    assert(holder.games("1234") == unchanged)
    verify(view).announce("1234", "Busted!")
  }

  test("Progress when key missing") {
    val view = mock[HyperView[Object]]
    val unchanged = mock[Object]

    val holder = new GamesHolder[Nothing, Object](null, view)
    setField(holder, "_games", Map("1234" -> unchanged))

    holder.progress("5678", null)

    assert(holder.games("1234") == unchanged)
    verify(view).announce("5678", "Nonexistent game 5678")
  }
}
