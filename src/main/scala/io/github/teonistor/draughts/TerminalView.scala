package io.github.teonistor.draughts

import java.lang.Math.max

class TerminalView {
  def display(game: Game): String = {
    val width = game.settings.boardSizes(game.settings.boardSizes.size - 2)
    val height = game.settings.boardSizes.last
    val gutter = max(2, (height - 1).toString.length)

    val bar = Array.fill(width)("───")
    val top = bar.mkString(" " * gutter + " ╭", "┬", "╮\n")
    val sep = bar.mkString(" " * gutter + " ├", "┼", "┤\n")
    val bot = bar.mkString(" " * gutter + " ╰", "┴", "╯\n")

    def displayOnePlane(higherCoords: Vector[Int]) =
      (0 until height).reverse.map(y =>
        (0 until width).map(x =>
          game.gameState.board.get(higherCoords ++ Vector(x, y)).map {
          case Piece.blackKing => " B "
          case Piece.blackPeon => " b "
          case Piece.whiteKing => " W "
          case Piece.whitePeon => " w "
        }
        .getOrElse("   "))
        .mkString("%%%dd │" format gutter format y, "│", "│"))
      .map(_+ "\n")
      .mkString(top, sep, bot) +
        (0 until width)
        .map("%3d" format _)
        .mkString("   ", " ", "\n")

    def displayOnePlaneWithHeader(higherCoords: Vector[Int]) =
      higherCoords.mkString("Board plane (", ", ", ", x, y)\n") + displayOnePlane(higherCoords)

    val footer =
      if (game.isGameOver)
        "Game over!\n"
      else game.gameState.ongoingJump
        .map(_.mkString("continue jumping from (", ", ", ") (or pass)"))
        .orElse(Some("move"))
        .map(game.gameState.currentPlayer + " to " +_+ ".\n")
        .get

    val higherDimensions = HDUtils.cartesianProduct(Vector.tabulate(game.settings.boardSizes.size - 2)(0 until game.settings.boardSizes(_)))
    if(higherDimensions.size == 1)
      displayOnePlane(higherDimensions.head) + footer
    else
      higherDimensions.map(displayOnePlaneWithHeader).mkString + footer
  }
}
