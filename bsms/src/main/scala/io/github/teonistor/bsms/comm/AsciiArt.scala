package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.core._
import io.github.teonistor.bsms.data.OceanCell.{damagedShip, mine}
import io.github.teonistor.bsms.data._

object AsciiArt {

  def illustrateGame(aliceState: AsciiDisplayablePlayerState,
                     bobState: AsciiDisplayablePlayerState,
                     settings: GameSettings): String = {
    val aStr = illustrateState(aliceState, bobState, settings)
    val bStr = illustrateState(bobState, aliceState, settings)

    illustrateGame0(
      aStr.lift.andThen(_.getOrElse("")),
      bStr.lift.andThen(_.getOrElse("")),
      aStr.length max bStr.length,
      aStr.map(_.length).max)
  }

  private def illustrateState(thisState: AsciiDisplayablePlayerState, thatState: AsciiDisplayablePlayerState, settings: GameSettings) =
    Vector(illustrateMines(thatState.state.board, thisState.cursor.opponentCursor, settings.width, settings.height),
        Vector(""),
        illustrateShips(thisState.state.board, thisState.overlay, thisState.cursor.ownCursor, settings.width, settings.height),
        Vector(""),
        illustrateInfo(thisState.state))
      .flatten

  private def illustrateGame0(a: Int => String, b: Int => String, howManyLines: Int, maxWidthA: Int) =
    (-1 to howManyLines).iterator
      .map(i => s"     ${a(i).padTo(maxWidthA, ' ')}     ║║     ${b(i)}")
      .map(_.stripTrailing())
      .mkString("\n")

  private val boxChars = Vector(" ", "F", "F", "╰", "F", "│", "╭", "├", "F", "╯", "─", "┴", "╮", "┤", "┬", "┼")
  private val cursorChars = Vector("F", "╲", "╱")
  private val damagedChar = "█"
  private val mineChar = "M"

  private[comm] def illustrateMines(board: OwnBoard, cursor: Option[Position], width: Int, height: Int) = {
    val mines = board.keySet.filter(board.get(_).contains(Left(mine)))
    val keepIfOnBoard = (x:Int, y:Int) => Some(Vector(x,y)).filter(p => p(0) > -1 && p(0) < width && p(1) > -1 && p(1) < height)

    (0 to height)
      .map(y => (0 to width * 3)
        .map { x =>

          val lx = (x + 2) / 3 - 1
          val rx =  x / 3
          val ty =  y - 1

          val tl = keepIfOnBoard(lx,ty)
          val tr = keepIfOnBoard(rx,ty)
          val bl = keepIfOnBoard(lx, y)
          val br = keepIfOnBoard(rx, y)

          if (lx == rx
              && cursor.contains(Vector(lx, y)))
            cursorChars(x % 3)

          else if (lx == rx
               && mines.contains(Vector(lx, y)))
            mineChar

          else
            boxChars((if (tl == tr) 0 else 1)
                   + (if (br == tr) 0 else 2)
                   + (if (bl == br) 0 else 4)
                   + (if (tl == bl) 0 else 8))
        })
      .map(_.mkString.stripTrailing())
  }

  private[comm] def illustrateShips(board: OwnBoard, overlay: Option[OwnBoard], cursor: Option[Position], width: Int, height: Int) = {
    val base = illustrateShips0(board, cursor, width, height)
    val over = illustrateShips0(overlay.getOrElse(Map.empty), None, width, height)

    (0 to height)
      .map(y => (0 to width * 3)
        .map { x => if (over(y)(x) == " ") base(y)(x) else over(y)(x) }
        .mkString.stripTrailing())
  }

  private def illustrateShips0(board: OwnBoard, cursor: Option[Position], width: Int, height: Int) = {
    val boardSafeGetName = board.lift
       .andThen(_.flatMap(_.toOption).map(_.name))

    (0 to height)
      .map(y => (0 to width * 3)
        .map { x =>

          val lx = (x + 2) / 3 - 1
          val rx =  x / 3
          val ty =  y - 1

          val tl = boardSafeGetName(Vector(lx,ty))
          val tr = boardSafeGetName(Vector(rx,ty))
          val bl = boardSafeGetName(Vector(lx, y))
          val br = boardSafeGetName(Vector(rx, y))

          if (lx == rx
              && cursor.contains(Vector(lx, y)))
            cursorChars(x % 3)

          else if (lx == rx
               && board.get(Vector(rx, y)).flatMap(_.toOption.map(_.parts(Vector(rx, y)))).contains(damagedShip))
            damagedChar

          else
            boxChars((if (tl == tr) 0 else 1)
                   + (if (br == tr) 0 else 2)
                   + (if (bl == br) 0 else 4)
                   + (if (tl == bl) 0 else 8))
        })
  }

  private[comm] def illustrateInfo(state: PlayerState) = state match {
    case PlayerStateShipPlacement(_,_, ships) =>
      if (state.isStageOver) Some("Ship placement complete. Waiting for other player")
      else ships.map { case ShipDescription(name, length) => s"> $name (length $length)" }.toList.prepended("Ship placement:")
    case PlayerStateMinePlacement(_,_, mines) =>
      if (state.isStageOver) Some("Mine placement complete. Waiting for other player")
      else Some("Mines to place: " + mines)
    case PlayerStateMovement(_,_, true) => Some("You may move")
    case PlayerStateMovement(_,_, false) => Some("You have moved. Waiting for other player")
    case PlayerStateShooting(_,_, true) => Some("Pick a target to shoot")
    case PlayerStateShooting(_,_, false) => Some("Shot fired. Waiting for other player")
  }
}
