package io.github.teonistor.draughts.srlz

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{DeserializationContext, SerializerProvider}
import io.github.teonistor.draughts.Piece

class PieceModule extends SimpleModule("PieceModule") {

  private val pieceToStr = Map(
    Piece.blackKing -> "BK",
    Piece.blackPeon -> "BP",
    Piece.whiteKing -> "WK",
    Piece.whitePeon -> "WP")
  private val strToPiece = pieceToStr.map {case (k,v) => (v,k)}

  addSerializer(classOf[Piece], (piece: Piece, json: JsonGenerator, _: SerializerProvider) =>
    pieceToStr.get(piece).map(json.writeString).getOrElse(json.writeNull()))

  addDeserializer(classOf[Piece], (parser: JsonParser, _: DeserializationContext) =>
    strToPiece.getOrElse(parser.getText(), null))
}

object PieceModule extends PieceModule {}
