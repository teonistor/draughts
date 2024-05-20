package io.github.teonistor.ttt.ui

import io.github.teonistor.ttt.GameState

trait View {
  def view(state: GameState)
  def announceWinner(winner: String)
}
