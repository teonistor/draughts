package io.github.teonistor.draughts

import io.github.teonistor.commongaming.View
import io.github.teonistor.draughts.data.Settings
import io.vavr.control.Validation

class Juncture(gameMaker: Settings=>Game, view: View[Game]) {

  private var game: Game =_

  def start(settings: Settings): Unit = {
    displayAndAssign(gameMaker(settings))
  }

  def progress(function: Game=>Validation[String,Game]): Unit =
    if (game != null)
      function(game).fold(view.announce, displayAndAssign)
    else
      view.announce("No game in progress")

  private def displayAndAssign(game:Game): Unit ={
    view.display(game)

    // TODO Here - record when an assignment last happened, so that later we can "garbage-collect" dead games
    this.game = game
  }
}