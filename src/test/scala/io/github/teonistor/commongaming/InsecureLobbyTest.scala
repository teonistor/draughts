package io.github.teonistor.commongaming

import org.mockito.BDDMockito.`given`
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike

class InsecureLobbyTest extends IdiomaticMockito with AnyFunSuiteLike {

  test("Create empty") {
    val lobby = new InsecureLobby[Nothing](null)

    assert(lobby.games.isEmpty)
  }

  test("New game with default name") {
    val gameFactory = mock[() => String]
    given(gameFactory()).willReturn("My game")

    val lobby = new InsecureLobby(gameFactory)
    lobby.newGame()

    assert(lobby.games.values.toList == List("My game"))
  }

  test("New game with custom name") {
    val gameFactory = mock[() => String]
    given(gameFactory()).willReturn("My game")

    val lobby = new InsecureLobby(gameFactory)
    lobby.newGame("My name")

    assert(lobby.games == Map("My name" -> "My game"))
  }
}
