package io.github.teonistor.draughts.srlz

import com.fasterxml.jackson.databind.json.JsonMapper
import io.github.teonistor.draughts.Player
import org.scalatest.funsuite.AnyFunSuiteLike

class PlayerModuleTest extends AnyFunSuiteLike {

  private val mapper = JsonMapper.builder()
    .addModule(PlayerModule)
    .build()

  Map(
    Player.black -> "B",
    Player.white -> "W").foreach { case (piece, str) =>

    test(s"Serialize player $piece") {
      assert(mapper.writeValueAsString(piece) == s"\"$str\"")
    }

    test(s"Deserialize player $piece") {
      assert(mapper.readValue(s"\"$str\"", classOf[Player]) == piece)
    }
  }

  test("Serialize null") {
    assert(mapper.writeValueAsString(null) == "null")
  }

  test("Deserialize rubbish") {
    assert(mapper.readValue("\"XX\"", classOf[Player]) == null)
  }
}
