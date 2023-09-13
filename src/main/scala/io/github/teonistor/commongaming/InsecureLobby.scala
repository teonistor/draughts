package io.github.teonistor.commongaming

import io.vavr.control.Validation

class InsecureLobby[SETTINGS,GAME](gameFactory: SETTINGS => GAME, hyperView: HyperView[GAME]) {

  private[this] var _games: Map[String, GAME] = Map.empty
  def games= _games

  def newGame(): Unit = {
    newGame(System.currentTimeMillis().toString)
  }

  def newGame(name: String): Unit = {
    _games = _games + (name -> gameFactory())
  }

  def progress(key:String, function: GAME=>Validation[String,GAME]): Unit =
    ???
}
