package io.github.teonistor.commongaming

trait HyperView[GAME] {
  def announce(key: String, message: String): Unit
  def announce(key: String, player: String, message: String): Unit
  def display(key: String, game: GAME): Unit
}
