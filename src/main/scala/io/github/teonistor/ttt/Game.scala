package io.github.teonistor.ttt

import io.github.teonistor.ttt.ui.{Input, View}

class Game(input: Input, view: View) {
  private val gameOverChecker = new GameOverChecker

  private var _state: GameState =_
  def state: GameState = _state

  private var _winner: Option[Player] = Option.empty
  def winner: Option[Player] = _winner

  def launch(): Runnable = {
    _state = GameState()
    view.view(state)
    () => playRound()
  }

  private def playRound() {
    val (i, j): (Int, Int) = input.takeInput()
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
    view.view(state)
    checkWinner()
  }

  private def checkWinner() {
    _winner = gameOverChecker.check(_state)
    if (_winner.isDefined) {
      view.announceWinner(_winner.get.toString)
      throw new ThreadDeath
    }
  }
}
