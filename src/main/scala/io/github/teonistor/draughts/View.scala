package io.github.teonistor.draughts

trait View {
  def announce(message: String): Unit
  def display(game: Game): Unit
}
