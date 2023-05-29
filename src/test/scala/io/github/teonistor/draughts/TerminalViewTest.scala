package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{GameState, Position, Settings}
import org.scalatest.funsuite.AnyFunSuite

class TerminalViewTest extends AnyFunSuite {

  test("3x3") {
    val game = new Game(null, null, null,
      Settings(3, 3, 1),
      GameState(Map(
        Position(0, 0) -> Piece.whitePeon,
        Position(0, 2) -> Piece.blackPeon,
        Position(2, 0) -> Piece.blackKing,
        Position(2, 2) -> Piece.whiteKing),
        Player.black))

    assert(new TerminalView().display(game) ==
      """   ╭───┬───┬───╮
        | 2 │ b │   │ W │
        |   ├───┼───┼───┤
        | 1 │   │   │   │
        |   ├───┼───┼───┤
        | 0 │ w │   │ B │
        |   ╰───┴───┴───╯
        |     0   1   2
        |""".stripMargin)
  }

  test("8x8") {
    val game = new Game(null, null, null,
      Settings(8, 8, 2),
      GameState(Map(
        Position(0, 0) -> Piece.whitePeon,
        Position(0, 4) -> Piece.blackPeon,
        Position(4, 6) -> Piece.blackKing,
        Position(7, 5) -> Piece.whiteKing),
        Player.black))

    assert(new TerminalView().display(game) ==
      """   ╭───┬───┬───┬───┬───┬───┬───┬───╮
        | 7 │   │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┤
        | 6 │   │   │   │   │ B │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┤
        | 5 │   │   │   │   │   │   │   │ W │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┤
        | 4 │ b │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┤
        | 3 │   │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┤
        | 2 │   │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┤
        | 1 │   │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┤
        | 0 │ w │   │   │   │   │   │   │   │
        |   ╰───┴───┴───┴───┴───┴───┴───┴───╯
        |     0   1   2   3   4   5   6   7
        |""".stripMargin)
  }

  test("12x12") {
    val game = new Game(null, null, null,
      Settings(12,12,4),
      GameState(Map(
        Position(0, 2) -> Piece.blackPeon,
        Position(8, 4) -> Piece.blackPeon,
        Position(4, 6) -> Piece.blackKing,
        Position(9, 9) -> Piece.whitePeon,
        Position(11,9) -> Piece.whiteKing,
        Position(7,11) -> Piece.whiteKing),
        Player.black))

    assert(new TerminalView().display(game) ==
      """   ╭───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───╮
        |11 │   │   │   │   │   │   │   │ W │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
        |10 │   │   │   │   │   │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
        | 9 │   │   │   │   │   │   │   │   │   │ w │   │ W │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
        | 8 │   │   │   │   │   │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
        | 7 │   │   │   │   │   │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
        | 6 │   │   │   │   │ B │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
        | 5 │   │   │   │   │   │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
        | 4 │   │   │   │   │   │   │   │   │ b │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
        | 3 │   │   │   │   │   │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
        | 2 │ b │   │   │   │   │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
        | 1 │   │   │   │   │   │   │   │   │   │   │   │   │
        |   ├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤
        | 0 │   │   │   │   │   │   │   │   │   │   │   │   │
        |   ╰───┴───┴───┴───┴───┴───┴───┴───┴───┴───┴───┴───╯
        |     0   1   2   3   4   5   6   7   8   9  10  11
        |""".stripMargin)
  }
}
