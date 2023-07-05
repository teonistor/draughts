package io.github.teonistor.draughts

import io.vavr.control.Validation

class Juncture(view:View) {
  private var game: Game =_

  def progress(function: Game=>Validation[String,Game]): Unit =
    if (game != null)
      function(game).fold(view.announce, displayAndAssign)
    else
      view.announce("No game in progress")

  private def displayAndAssign(game:Game): Unit ={
    view.display(game)
    this.game = game
  }

}
