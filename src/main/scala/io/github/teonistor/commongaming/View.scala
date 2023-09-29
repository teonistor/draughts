package io.github.teonistor.commongaming

trait View[GAME] {
  def announce(message: String): Unit
  def announce(player: String, message: String): Unit
  def display(game: GAME): Unit
}
