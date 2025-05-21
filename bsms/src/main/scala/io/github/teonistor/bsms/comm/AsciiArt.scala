package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.core._
import io.github.teonistor.bsms.data.OceanCell.damagedShip
import io.github.teonistor.bsms.data._

object AsciiArt {

  def illustrateGame(game: BattleshipMinesweeper,
                     aliceCursor: Option[Position] = None,
                     bobCursor: Option[Position] = None): String = {
    val a = illustrateState(game.aliceState, aliceCursor, game.settings.width, game.settings.height).linesIterator.to(Vector)
    val b = illustrateState(game.bobState, bobCursor, game.settings.width, game.settings.height).linesIterator.to(Vector)

    illustrateGame0(
      a.lift.andThen(_.getOrElse("")),
      b.lift.andThen(_.getOrElse("")),
      a.length max b.length,
      a.map(_.length).max)
  }

  private def illustrateGame0(a: Int => String, b: Int => String, howManyLines: Int, maxWidthA: Int) =
    (-1 to howManyLines).iterator
      .map(i => s"     ${a(i).padTo(maxWidthA, ' ')}     ║║     ${b(i)}")
      .map(_.stripTrailing())
      .mkString("\n")

  def illustrateState(state: PlayerState, cursor: Option[Position], width: Int, height: Int) =
    if (state.isInstanceOf[PlayerStateMinePlacement] || state.isInstanceOf[PlayerStateShooting])
      // We'll think later about showing limited knowledge about the adversary's ocean
      s"${illustrateBoard(Map.empty, cursor, width, height)}\n${illustrateStateInner(state)}"
    else
      s"${illustrateBoard(state.board, cursor, width, height)}\n${illustrateStateInner(state)}"

  private val boxChars = Vector(" ", "F", "F", "╰", "F", "│", "╭", "├", "F", "╯", "─", "┴", "╮", "┤", "┬", "┼")
  private val cursorChars = Vector("F", "╲", "╱")
  private val damagedChar = "█"

  def illustrateBoard(board: OwnBoard, cursor: Option[Position], width: Int, height: Int): String = {
    // If and when we display adversary information of a different nature, we'll need something other than an "empty" check
    val empty = board.isEmpty

    val boarder =
      if(empty)
        (p: Position) => if (p(0) < 0 || p(0) >=width || p(1) < 0 || p(1) >=height )
          None
        else
          Some(s"${p(0)},${p(1)}")
      else
        board.lift.andThen(_.flatMap(_.toOption).map(_.name))

    (0 to height)
      .map(y => (0 to width * 3)
        .map { x =>

          val lx = (x + 2) / 3 - 1
          val rx =  x / 3
          val ty =  y - 1

          val tl = boarder(Vector(lx,ty))
          val tr = boarder(Vector(rx,ty))
          val bl = boarder(Vector(lx, y))
          val br = boarder(Vector(rx, y))

          if (lx == rx
              && cursor.contains(Vector(lx, y)))
            cursorChars(x % 3)

          else if (!empty
              && lx == rx
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
