package io.github.teonistor.commongaming

import org.mockito.Mockito.verify
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.test.util.ReflectionTestUtils.{getField, setField}

class InsecureLobbyTest extends IdiomaticMockito with AnyFunSuiteLike {

  test("create") {
    val ws = mock[SimpMessagingTemplate]
    val lobby = new InsecureLobby(ws)

    lobby.create("aaa", GameConfiguration("Pog", Set("Pogger", "Poggee")))

    val expected = Map("aaa" -> UserGameAllocation("aaa", "Pog", Map.empty, Set("Pogger", "Poggee")))
    assert(getField(lobby, "allocations") == expected)
    verify(ws).convertAndSend("/lobby/state", expected)
  }

  test("remove") {
    // meta-TODO Can we come up with a way for games not to have to deregister, but naturally "fall out of scope"? Like a WeakReference...
    assert(false)
  }

  test("allocate to nonexistent game does nothing") {
    val lobby = new InsecureLobby(null)
    lobby.allocate(("7", "a", "b"))

    assert(getField(lobby, "allocations").asInstanceOf[Map[_, _]].isEmpty)
  }

  test("allocate to not unallocated player does nothing") {
    val lobby = new InsecureLobby(null)
    val allocations = Map("7" -> UserGameAllocation("7", "Zilch", Map("a" -> "y"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.allocate(("7", "a", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("allocate user already allocated to other game to unallocated player does nothing") {
    val lobby = new InsecureLobby(null)
    val allocations = Map(
      "7" -> UserGameAllocation("7", "Zilch", Map("a" -> "x", "b" -> "y"), Set("c", "d")),
      "9" -> UserGameAllocation("9", "Zorch", Map("a" -> "z"), Set("b", "c")))
    setField(lobby, "allocations", allocations)

    lobby.allocate(("9", "c", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("allocate user to unallocated player") {
    val ws = mock[SimpMessagingTemplate]
    val lobby = new InsecureLobby(ws)
    setField(lobby, "allocations", Map("7" -> UserGameAllocation("7", "Zilch", Map("a" -> "x", "b" -> "y"), Set("c", "d"))))

    lobby.allocate(("7", "c", "x"))

    val expected = Map("7" -> UserGameAllocation("7", "Zilch", Map("a" -> "x", "b" -> "y", "c" -> "x"), Set("d")))
    assert(getField(lobby, "allocations") == expected)
    verify(ws).convertAndSend("/lobby/state", expected)
  }


  test("deallocate from nonexistent game does nothing") {
    val lobby = new InsecureLobby(null)
    val allocations = Map(7 -> UserGameAllocation("7", "Zilch", Map("a" -> "x"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.deallocate(("9", "a", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("deallocate from not allocated player does nothing") {
    val lobby = new InsecureLobby(null)
    val allocations = Map(7 -> UserGameAllocation("7", "Zorch", Map("a" -> "x"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.deallocate(("7", "c", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("deallocate not allocated user does nothing") {
    val lobby = new InsecureLobby(null)
    val allocations = Map(7 -> UserGameAllocation("7", "Zilch", Map("a" -> "x"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.deallocate(("7", "a", "y"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("deallocate") {
    val ws = mock[SimpMessagingTemplate]
    val lobby = new InsecureLobby(ws)
    setField(lobby, "allocations", Map("7" -> UserGameAllocation("7", "Zilch", Map("a" -> "x", "b" -> "x"), Set("c"))))

    lobby.deallocate(("7", "a", "x"))

    val expected = Map("7" -> UserGameAllocation("7", "Zilch", Map("b" -> "x"), Set("a", "c")))
    assert(getField(lobby, "allocations") == expected)
    verify(ws).convertAndSend("/lobby/state", expected)
  }
}