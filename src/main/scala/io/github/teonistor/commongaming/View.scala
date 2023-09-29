package io.github.teonistor.commongaming

import io.github.teonistor.draughts.Game

trait View {
  def announce(message: String): Unit
  def announce(player: String, message: String): Unit
  def display(game: Game): Unit
}
