package io.github.teonistor.commongaming

import scala.reflect.ClassTag

sealed trait GameConfiguration {
  def name: String
  def create(message: String): Unit
  def requiredPlayers: Set[String]
}

class SimpleGameConfiguration[SETTINGS: ClassTag, GAME](
      val name: String,
      val requiredPlayers: Set[String],
      holder: GamesHolder[Nothing, GAME],
      factory: SETTINGS => GAME) extends GameConfiguration {

  def create(message:String): Unit = ???
}
