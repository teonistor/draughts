package io.github.teonistor.commongaming

import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike
import org.springframework.test.util.ReflectionTestUtils.{getField, setField}

//@AutoConfigureMockMvc
//@SpringBootTest(classes=Array(classOf[InsecureLobby]))
//class InsecureLobbyTest {
//
//  @Autowired private val mockMvc: MockMvc = null
//
//  @MockBean private val ws: SimpMessagingTemplate = null
//
//  @Test
//  def playerId(): Unit = {
//    val string = mockMvc.perform(post("/api/lobby"))
//      .andReturn().getResponse.getContentAsString
//
//    assert(string == "abc")
//  }
//}

class InsecureLobbyTest extends IdiomaticMockito with AnyFunSuiteLike {

  test("allocate to nonexistent game does nothing") {
    val lobby = new InsecureLobby(null)
    lobby.allocate((7, "a", "b"))

    assert(getField(lobby, "allocations").asInstanceOf[Map[_,_]].isEmpty)
  }

  test("allocate to not unallocated player does nothing") {
    val lobby = new InsecureLobby(null)
    setField(lobby, "allocations", Map(7 -> UserGameAllocation(7, null, Map("a" -> "y"), Set("b"))))

    lobby.allocate((7, "a", "x"))

    assert(getField(lobby, "allocations") == Map(7 -> UserGameAllocation(7, null, Map("a" -> "y"), Set("b"))))
  }

  test("allocate user already allocated to other game to unallocated player does nothing") {
    val lobby = new InsecureLobby(null)
    val allocations = Map(
      7 -> UserGameAllocation(7, null, Map("a" -> "x", "b" -> "y"), Set("c", "d")),
      9 -> UserGameAllocation(9, null, Map("a" -> "z"), Set("b", "c")))
    setField(lobby, "allocations", allocations)

    lobby.allocate((9, "c", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("allocate user to unallocated player") {
    val lobby = new InsecureLobby(null)
    setField(lobby, "allocations", Map(7 -> UserGameAllocation(7, null, Map("a" -> "x", "b" -> "y"), Set("c", "d"))))

    lobby.allocate((7, "c", "x"))

    assert(getField(lobby, "allocations") == Map(7 -> UserGameAllocation(7, null, Map("a" -> "x", "b" -> "y", "c" -> "x"), Set("d"))))
  }


  test("deallocate from nonexistent game does nothing") {
    val lobby = new InsecureLobby(null)
    val allocations = Map(7 -> UserGameAllocation(7, null, Map("a" -> "x"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.deallocate((9, "a", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("deallocate from not allocated player does nothing") {
    val lobby = new InsecureLobby(null)
    val allocations = Map(7 -> UserGameAllocation(7, null, Map("a" -> "x"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.deallocate((7, "c", "x"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("deallocate not allocated user does nothing") {
    val lobby = new InsecureLobby(null)
    val allocations = Map(7 -> UserGameAllocation(7, null, Map("a" -> "x"), Set("b")))
    setField(lobby, "allocations", allocations)

    lobby.deallocate((7, "a", "y"))

    assert(getField(lobby, "allocations") == allocations)
  }

  test("deallocate") {
    val lobby = new InsecureLobby(null)
    setField(lobby, "allocations", Map(7 -> UserGameAllocation(7, null, Map("a" -> "x", "b" -> "x"), Set("c"))))

    lobby.deallocate((7, "a", "x"))

    assert(getField(lobby, "allocations") == Map(7 -> UserGameAllocation(7, null, Map("b" -> "x"), Set("a", "c"))))
  }
}
