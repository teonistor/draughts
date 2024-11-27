package io.github.teonistor.connectn.termview

import io.github.teonistor.connectn.Game

object TermView {

  def display[T](game: Game, continuation: String => T = println): T = {
    if (game.settings.baseDimensions.size > 1)
      throw new UnsupportedOperationException("Currently only supporting 1-dimensional base")

    val board = (0 until game.settings.height).iterator
      .map(row => (0 until game.settings.baseDimensions(0)).iterator
        .map(col => game.currentState
          .board.get(Vector(col))
          .flatMap(column => column.lift(row - game.settings.height + column.size))
          .map(" " + _.name()(0))
          .getOrElse("  "))
        .mkString)
      .mkString("\n")
    val rim = (0 until game.settings.baseDimensions(0))
      .map(_ % 10)
      .mkString(" ", " ", "")
    val nextLine = s" ${game.currentState.currentPlayer} to move"

    continuation(board + "\n" + rim + "\n" + nextLine)
  }
}
