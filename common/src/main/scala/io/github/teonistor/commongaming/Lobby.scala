package io.github.teonistor.commongaming

trait Lobby {
  def create(key:String, gameConfiguration:GameConfiguration): Unit
}
