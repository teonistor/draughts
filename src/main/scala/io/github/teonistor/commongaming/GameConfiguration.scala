package io.github.teonistor.commongaming

import com.fasterxml.jackson.databind.JsonNode

import scala.reflect.ClassTag

sealed trait GameConfiguration {
  def name: String
  def create(key:Long, settings:JsonNode): Unit
  def requiredPlayers: Set[String]
}

class SimpleGameConfiguration[SETTINGS: ClassTag, GAME](
      val name: String,
      val requiredPlayers: Set[String],
      holder: GamesHolder[Nothing, GAME],
      factory: SETTINGS => GAME) extends GameConfiguration {

  def create(key:Long, settings:JsonNode): Unit = ???
}
