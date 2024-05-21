package io.github.teonistor.commongaming

import org.mockito.Mockito.verify
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.test.util.ReflectionTestUtils.{getField, setField}

class InsecureLobbyTest extends IdiomaticMockito with AnyFunSuiteLike {

  // How to test websocket servers, or something
  // https://github.com/rstoyanchev/spring-websocket-portfolio/tree/main/src/test/java/org/springframework/samples/portfolio/web

  test("create") {
    val ws = mock[SimpMessagingTemplate]
    val lobby = new InsecureLobby(ws, null)

    lobby.create("aaa", GameConfiguration("Pog", Set("Pogger", "Poggee")))

    val expected = Map("aaa" -> UserGameAllocation("aaa", "Pog", Map.empty, Set("Pogger", "Poggee")))
    assert(getField(lobby, "allocations") == expected)
    verify(ws).convertAndSend("/lobby/state", expected)
  }

  test("Cannot create if limit reached") {
    val ws = mock[SimpMessagingTemplate]
    val lobby = new InsecureLobby(ws, null)
    val pog = GameConfiguration("Pog", Set("Pogger", "Poggee"))
    setField(lobby, "allocations", ('a' to 'j').groupMapReduce(_.toString * 3)(_ => pog)((l, _) => l))

    assert(intercept[IllegalStateException](lobby.create("zzz", pog)).getMessage == "Maximum number of games (10) reached")
    assert(getField(lobby, "allocations").asInstanceOf[Iterable[_]].size == 10)
  }

  test("Cannot create with repeated key") {
    val ws = mock[SimpMessagingTemplate]
    val lobby = new InsecureLobby(ws, null)
    val pog = GameConfiguration("Pog", Set("Pogger", "Poggee"))
    setField(lobby, "allocations", Map("pog" -> pog))

    assert(intercept[IllegalArgumentException](lobby.create("pog", pog)).getMessage == "Key 'pog' already in use")
    assert(getField(lobby, "allocations").asInstanceOf[Iterable[_]].size == 1)
  }

  test("remove") {
    // meta-TODO Can we come up with a way for games not to have to deregister, but naturally "fall out of scope"? Like a WeakReference...
//    assert(false)
//
//    new AbstractReferenceMap() {
//
//  }
  }

  test("allocate to nonexistent game does nothing") {
    val lobby = new InsecureLobby(null, null)
    lobby.allocate(("7", "a", "b"))

    assert(getField(lobby, "allocations").asInstanceOf[Map[_, _]].isEmpty)
  }

  test("allocate to not unallocated player does nothing") {
    val lobby = new InsecureLobby(null, null)
    val allocations = Map("7" -> UserGameAllocation("7", "Zilch", Map("a" -> "y"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.allocate(("7", "a", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("allocate user already allocated to other game to unallocated player does nothing") {
    val lobby = new InsecureLobby(null, null)
    val allocations = Map(
      "7" -> UserGameAllocation("7", "Zilch", Map("a" -> "x", "b" -> "y"), Set("c", "d")),
      "9" -> UserGameAllocation("9", "Zorch", Map("a" -> "z"), Set("b", "c")))
    setField(lobby, "allocations", allocations)

    lobby.allocate(("9", "c", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("allocate user to unallocated player") {
    val ws = mock[SimpMessagingTemplate]
    val lobby = new InsecureLobby(ws, null)
    setField(lobby, "allocations", Map("7" -> UserGameAllocation("7", "Zilch", Map("a" -> "x", "b" -> "y"), Set("c", "d"))))

    lobby.allocate(("7", "c", "x"))

    val expected = Map("7" -> UserGameAllocation("7", "Zilch", Map("a" -> "x", "b" -> "y", "c" -> "x"), Set("d")))
    assert(getField(lobby, "allocations") == expected)
    verify(ws).convertAndSend("/lobby/state", expected)
  }


  test("deallocate from nonexistent game does nothing") {
    val lobby = new InsecureLobby(null, null)
    val allocations = Map(7 -> UserGameAllocation("7", "Zilch", Map("a" -> "x"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.deallocate(("9", "a", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("deallocate from not allocated player does nothing") {
    val lobby = new InsecureLobby(null, null)
    val allocations = Map(7 -> UserGameAllocation("7", "Zorch", Map("a" -> "x"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.deallocate(("7", "c", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("deallocate not allocated user does nothing") {
    val lobby = new InsecureLobby(null, null)
    val allocations = Map(7 -> UserGameAllocation("7", "Zilch", Map("a" -> "x"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.deallocate(("7", "a", "y"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("deallocate") {
    val ws = mock[SimpMessagingTemplate]
    val lobby = new InsecureLobby(ws, null)
    setField(lobby, "allocations", Map("7" -> UserGameAllocation("7", "Zilch", Map("a" -> "x", "b" -> "x"), Set("c"))))

    lobby.deallocate(("7", "a", "x"))

    val expected = Map("7" -> UserGameAllocation("7", "Zilch", Map("b" -> "x"), Set("a", "c")))
    assert(getField(lobby, "allocations") == expected)
    verify(ws).convertAndSend("/lobby/state", expected)
  }
}