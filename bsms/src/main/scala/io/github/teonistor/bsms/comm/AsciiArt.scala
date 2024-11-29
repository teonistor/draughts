package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.core._
import io.github.teonistor.bsms.data.OceanCell.damagedShip
import io.github.teonistor.bsms.data._

object AsciiArt {

  def illustrateGame(game: BattleshipMinesweeper): String = ???

  def illustrateState(state: PlayerState, width: Int, height: Int): String =
      s"${illustrateBoard(state.board, width, height)}\n${illustrateStateInner(state)}"

  private val boxChars = Vector(" ", "F", "F", "╰", "F", "│", "╭", "├", "F", "╯", "─", "┴", "╮", "┤", "┬", "┼")
  private val damagedChar = "█"

  def illustrateBoard(board: OwnBoard, width: Int, height: Int): String = {
    (0 to height)
      .map(y => (0 to width * 3)
        .map { x =>

          val lx = (x + 2) / 3 - 1
          val rx =  x / 3
          val ty =  y - 1

          val tl = board.get(Vector(lx,ty)).flatMap(_.toOption).map(_.name)
          val tr = board.get(Vector(rx,ty)).flatMap(_.toOption).map(_.name)
          val bl = board.get(Vector(lx, y)).flatMap(_.toOption).map(_.name)
          val br = board.get(Vector(rx, y)).flatMap(_.toOption).map(_.name)

          if (lx == rx
              && board.get(Vector(rx, y)).flatMap(_.toOption.map(_.parts(Vector(rx, y)))).contains(damagedShip))
            damagedChar
          else
            boxChars((if (tl == tr) 0 else 1)
                   + (if (br == tr) 0 else 2)
                   + (if (bl == br) 0 else 4)
                   + (if (tl == bl) 0 else 8))
        })
      .map(_.mkString.stripTrailing())
      .mkString("\n")
  }

  private def illustrateStateInner(state: PlayerState) = state match {
    case PlayerStateShipPlacement(_,_, ships) =>
      if (state.isStageOver) "Ship placement complete. Waiting for other player"
      else ships.map { case ShipDescription(name, length) => s"> $name (length $length)" }.mkString("Ship placement:\n", "\n", "")
    case PlayerStateMinePlacement(_,_, mines) =>
      if (state.isStageOver) "Mine placement complete. Waiting for other player"
      else "Mines to place: " + mines
    case PlayerStateMovement(_,_, true) => "You may move"
    case PlayerStateMovement(_,_, false) => "You have moved. Waiting for other player"
    case PlayerStateShooting(_,_, true) => "Pick a target to shoot"
    case PlayerStateShooting(_,_, false) => "Shot fired. Waiting for other player"
  }
}
