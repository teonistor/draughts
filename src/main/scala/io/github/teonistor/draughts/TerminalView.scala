package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.Position

import java.lang.Math.max

class TerminalView {
  def display(game: Game): String = {
    val gutter = max(2, (game.settings.boardHeight - 1).toString.length)

    val bar = Array.fill(game.settings.boardWidth)("───")
    val top = bar.mkString(" " * gutter + " ╭", "┬", "╮\n")
    val sep = bar.mkString(" " * gutter + " ├", "┼", "┤\n")
    val bot = bar.mkString(" " * gutter + " ╰", "┴", "╯\n")

    (0 until game.settings.boardHeight).reverse.map(y =>
      (0 until game.settings.boardWidth).map(x =>
        game.gameState.board.get(Position(x,y)).map {
          case Piece.blackKing => " B "
          case Piece.blackPeon => " b "
          case Piece.whiteKing => " W "
          case Piece.whitePeon => " w "
        }
        .getOrElse("   "))
        .mkString("%%%dd │" format gutter format y, "│", "│"))
      .map(_+ "\n")
      .mkString(top, sep, bot) +
      (0 until game.settings.boardWidth)
        .map("%3d" format _)
        .mkString("   ", " ", "\n") +
      (if (game.isGameOver)
        "Game over!\n"
      else game.gameState.ongoingJump
        .map("continue jumping from " +_+ " (or pass)")
        .orElse(Some("move"))
        .map(game.gameState.currentPlayer + " to " +_+ ".\n")
        .get)
  }
}
