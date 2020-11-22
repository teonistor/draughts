package io.github.teonistor.ttt

class Game extends Runnable {
  private val terminalInput = new TerminalInput
  private val terminalView = new TerminalView

  var state: GameState =_

  override def run(): Unit = {
    if (state == null) {
      state = GameState()
      terminalView.view(state)
    }

    val (i, j): (Int, Int) = terminalInput.takeInput()
    if (validMove(i, j)) {
      state = state.advance(i, j)
      terminalView.view(state)
    }
  }

  private def validMove(i: Int, j: Int) = {
    val (left, right, top, bottom) = state.margins
    state.free(i, j) && top <= i && i <= bottom && left <= j && j <= right
  }
}
