package io.github.teonistor.commongaming

import io.vavr.control.Validation
import io.vavr.control.Validation.invalid

class GamesHolder[GAME,-SETTINGS](gameMaker: SETTINGS => GAME,
                                  hyperView: HyperView[GAME],
                                  gameConfiguration: GameConfiguration,
                                  onStart: GameStartedCallback[GAME]) {

  private[this] var _games: Map[String, GAME] = Map.empty

  def games = _games

  def start(settings: SETTINGS): Unit = {
    val generatedKey = System.currentTimeMillis().toString
    val game = gameMaker(settings)
    onStart(generatedKey, gameConfiguration, game)
    displayAndAssign(generatedKey, game)
  }

  def progress(key: String, function: GAME => Validation[String, GAME]): Unit =
    _games.get(key)
      .fold[Validation[String, GAME]](invalid("Nonexistent game " + key))(function)
      .fold(hyperView.announce(key, _), displayAndAssign(key, _))

  private def displayAndAssign(key: String, game: GAME): Unit = {
    hyperView.display(key, game)

    // TODO Here - record when an assignment last happened, so that later we can "garbage-collect" dead games
    _games = _games.updated(key, game)
  }
}
