package io.github.teonistor.draughts.srlz

import com.fasterxml.jackson.databind.json.JsonMapper
import io.github.teonistor.draughts.Piece
import org.scalatest.funsuite.AnyFunSuiteLike

class PieceModuleTest extends AnyFunSuiteLike {

  private val mapper = JsonMapper.builder()
    .addModule(PieceModule)
    .build()

  Map(
    Piece.blackKing -> "BK",
    Piece.blackPeon -> "BP",
    Piece.whiteKing -> "WK",
    Piece.whitePeon -> "WP").foreach { case (piece, str) =>

    test(s"Serialize piece $piece") {
      assert(mapper.writeValueAsString(piece) == s"\"$str\"")
    }

    test(s"Deserialize piece $piece") {
      assert(mapper.readValue(s"\"$str\"", classOf[Piece]) == piece)
    }
  }

  test("Serialize null") {
    assert(mapper.writeValueAsString(null) == "null")
  }

  test("Deserialize rubbish") {
    assert(mapper.readValue("\"XX\"", classOf[Piece]) == null)
  }
}
