package io.github.teonistor.ttt

class Game extends Runnable {
  private val terminalInput = new TerminalInput
  private val terminalView = new TerminalView
  private val gameOverChecker = new GameOverChecker

  private var _state: GameState =_
  def state: GameState = _state

  private var _winner: Option[Player] = Option.empty
  def winner: Option[Player] = _winner

  override def run(): Unit = {
    if (state == null) {
      _state = GameState()
      terminalView.view(state)
    }

    val (i, j): (Int, Int) = terminalInput.takeInput()
    if (validMove(i, j)) {
      updateState(i, j)
    }
  }

  private def validMove(i: Int, j: Int) = {
    val (left, right, top, bottom) = state.margins
    state.free(i, j) && top <= i && i <= bottom && left <= j && j <= right
  }

  private def updateState(i: Int, j: Int) {
    _state = state.advance(i, j)
    terminalView.view(state)
    checkWinner()
  }

  private def checkWinner() {
    _winner = gameOverChecker.isGameOver(_state)
    if (_winner.isDefined) {
      terminalView.announce(s"${_winner.get} wins!")
      throw new ThreadDeath
    }
  }
}
