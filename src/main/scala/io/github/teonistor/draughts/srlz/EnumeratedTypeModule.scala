package io.github.teonistor.draughts.srlz

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{DeserializationContext, SerializerProvider}

import scala.reflect.ClassTag

class EnumeratedTypeModule[T: ClassTag](name: String, objToStr: Map[T, String]) extends SimpleModule(name) {

  private val strToObj = objToStr.map { case (k, v) => (v, k) }

  addSerializer(summonClass, (piece: T, json: JsonGenerator, _: SerializerProvider) =>
    objToStr.get(piece).map(json.writeString).getOrElse(json.writeNull()))

  addDeserializer(summonClass, (parser: JsonParser, _: DeserializationContext) =>
    strToObj.getOrElse(parser.getText(), null))

  private def summonClass[C] =
    implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[C]]
}
