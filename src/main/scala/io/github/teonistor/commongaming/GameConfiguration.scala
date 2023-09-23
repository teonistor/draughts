package io.github.teonistor.commongaming

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}

import scala.reflect.ClassTag

sealed trait GameConfiguration {
  def name: String
  def create(key:Long, settings:JsonNode): Unit
  def requiredPlayers: Set[String]
}

class SimpleGameConfiguration[SETTINGS: ClassTag, GAME](
      val name: String,
      val requiredPlayers: Set[String],
      objectMapper: ObjectMapper,
      holder: GamesHolder[GAME],
      factory: SETTINGS => GAME) extends GameConfiguration {

  def create(key:Long, settings:JsonNode): Unit = {
    val valueType: Class[SETTINGS] = implicitly[ClassTag[SETTINGS]].runtimeClass.asInstanceOf[Class[SETTINGS]]
    println(valueType)
    val settings1 = objectMapper.treeToValue(settings, valueType)
    println(settings1)
    holder.add(key, factory(settings1))
  }
}
