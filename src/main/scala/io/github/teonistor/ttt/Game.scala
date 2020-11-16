package io.github.teonistor.ttt

class Game extends Runnable{
  private val terminalInput = new TerminalInput
  private val terminalView = new TerminalView

  var state: GameState =_

  override def run(): Unit = {
    state = GameState()

    while(true) {
      val xIn: (Int, Int) = terminalInput.takeInput()
      state = state.x(xIn._1, xIn._2)
      terminalView.view(state)

      val oIn: (Int, Int) = terminalInput.takeInput()
      state = state.o(oIn._1, oIn._2)
      terminalView.view(state)
    }
  }
}

object Game {
  def main(args: Array[String]): Unit = {
    new Game().run()
  }
}
