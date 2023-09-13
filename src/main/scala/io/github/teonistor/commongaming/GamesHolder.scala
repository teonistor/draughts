package io.github.teonistor.commongaming

import io.vavr.control.Validation
import io.vavr.control.Validation.invalid

class GamesHolder[SETTINGS, GAME](gameFactory: SETTINGS => GAME, hyperView: HyperView[GAME]) {

  private[this] var _games: Map[String, GAME] = Map.empty

  def games = _games

  def newGame(settings: SETTINGS): Unit = {
    _games = _games + (System.currentTimeMillis().toString -> gameFactory(settings))
  }

  def progress(key: String, function: GAME => Validation[String, GAME]): Unit =
    _games.get(key)
      .fold[Validation[String, GAME]](invalid("Nonexistent game " + key))(function)
      .fold(hyperView.announce(key, _), displayAndAssign(key, _))

  private def displayAndAssign(key: String, game: GAME): Unit = {
    hyperView.display(key, game)
    _games = _games.updated(key, game)
  }
}
