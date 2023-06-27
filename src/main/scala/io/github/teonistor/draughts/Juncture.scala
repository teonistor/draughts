package io.github.teonistor.draughts

import io.vavr.control.Validation

class Juncture {
  private var game: Game =_

  def progress(function: Game=>Validation[String,Game]): Unit =
     function(game).toOption.forEach(game =_)

}
