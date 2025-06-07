package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.data.Position
import org.mockito.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuite

class CursorTest extends AnyFunSuite with IdiomaticMockito {

  private val position = mock[Position]

  test("none") {
      assert(Cursor.none.ownCursor.isEmpty)
      assert(Cursor.none.opponentCursor.isEmpty)
  }

  test("ownCursor") {
    val own = Cursor.own(position)
    assert(own.ownCursor contains position)
    assert(own.opponentCursor.isEmpty)
  }

  test("opponentCursor") {
    val own = Cursor.opponent(position)
    assert(own.ownCursor.isEmpty)
    assert(own.opponentCursor contains position)
  }
}
