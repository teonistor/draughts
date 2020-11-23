package io.github.teonistor.ttt

class Game {
  private val terminalInput = new TerminalInput
  private val terminalView = new TerminalView
  private val gameOverChecker = new GameOverChecker

  private var _state: GameState =_
  def state: GameState = _state

  private var _winner: Option[Player] = Option.empty
  def winner: Option[Player] = _winner

  def launch(): Runnable = {
    _state = GameState()
    terminalView.view(state)
    () => playRound()
  }

  private def playRound() {
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
    _winner = gameOverChecker.check(_state)
    if (_winner.isDefined) {
      terminalView.announceWinner(_winner.get.toString)
      throw new ThreadDeath
    }
  }
}
