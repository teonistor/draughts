package io.github.teonistor.commongaming

import org.mockito.Mockito.verify
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.test.util.ReflectionTestUtils.{getField, setField}

import java.util.List.{of => juList}

class InsecureLobbyTest extends IdiomaticMockito with AnyFunSuiteLike {

  test("Cannot construct with name-conflicting configurations") {
    assertThrows[IllegalArgumentException](new InsecureLobby(null, juList(
      new SimpleGameConfiguration("banana", Set.empty, null, null),
      new SimpleGameConfiguration("banana", Set.empty, null, null))))
  }
  
  
  test("allocate to nonexistent game does nothing") {
    val lobby = new InsecureLobby(null,null)
    lobby.allocate((7, "a", "b"))

    assert(getField(lobby, "allocations").asInstanceOf[Map[_,_]].isEmpty)
  }

  test("allocate to not unallocated player does nothing") {
    val lobby = new InsecureLobby(null,null)
    val allocations = Map(7 -> UserGameAllocation(7, Map("a" -> "y"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.allocate((7, "a", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("allocate user already allocated to other game to unallocated player does nothing") {
    val lobby = new InsecureLobby(null,null)
    val allocations = Map(
      7 -> UserGameAllocation(7, Map("a" -> "x", "b" -> "y"), Set("c", "d")),
      9 -> UserGameAllocation(9, Map("a" -> "z"), Set("b", "c")))
    setField(lobby, "allocations", allocations)

    lobby.allocate((9, "c", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("allocate user to unallocated player") {
    val ws = mock[SimpMessagingTemplate]
    val lobby = new InsecureLobby(ws, juList())
    setField(lobby, "allocations", Map(7 -> UserGameAllocation(7, Map("a" -> "x", "b" -> "y"), Set("c", "d"))))

    lobby.allocate((7, "c", "x"))

    val expected = Map(7 -> UserGameAllocation(7, Map("a" -> "x", "b" -> "y", "c" -> "x"), Set("d")))
    assert(getField(lobby, "allocations") == expected)
    verify(ws).convertAndSend("/lobby/lobby-state", expected)
  }


  test("deallocate from nonexistent game does nothing") {
    val lobby = new InsecureLobby(null,null)
    val allocations = Map(7 -> UserGameAllocation(7, Map("a" -> "x"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.deallocate((9, "a", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("deallocate from not allocated player does nothing") {
    val lobby = new InsecureLobby(null,null)
    val allocations = Map(7 -> UserGameAllocation(7, Map("a" -> "x"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.deallocate((7, "c", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("deallocate not allocated user does nothing") {
    val lobby = new InsecureLobby(null,null)
    val allocations = Map(7 -> UserGameAllocation(7, Map("a" -> "x"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.deallocate((7, "a", "y"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("deallocate") {
    val ws = mock[SimpMessagingTemplate]
    val lobby = new InsecureLobby(ws, juList())
    setField(lobby, "allocations", Map(7 -> UserGameAllocation(7, Map("a" -> "x", "b" -> "x"), Set("c"))))

    lobby.deallocate((7, "a", "x"))

    val expected = Map(7 -> UserGameAllocation(7, Map("b" -> "x"), Set("a", "c")))
    assert(getField(lobby, "allocations") == expected)
    verify(ws).convertAndSend("/lobby/lobby-state", expected)
  }


//  test()
}
