package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.{GameState, Settings}
import org.scalatest.funsuite.AnyFunSuite
import org.springframework.test.util.ReflectionTestUtils.setField

class TerminalViewTest extends AnyFunSuite {

  test("3x3") {
    val game = new Game(null, null, null,
      Settings(3, 3, 1),
      GameState(Map(
        Vector(0, 0) -> Piece.whitePeon,
        Vector(0, 2) -> Piece.blackPeon,
        Vector(2, 0) -> Piece.blackKing,
        Vector(2, 2) -> Piece.whiteKing),
        Player.white,
        None))

    setField(game, "isGameOver", false)
    setField(game, "bitmap$0", 3.asInstanceOf[Byte])

    assert(new TerminalView().display(game) ==
      """   ╭───┬───┬───╮
        | 2 │ b │   │ W │
        |   ├───┼───┼───┤
        | 1 │   │   │   │
        |   ├───┼───┼───┤
        | 0 │ w │   │ B │
        |   ╰───┴───┴───╯
        |     0   1   2
        |White to move.
        |""".stripMargin)
  }

  test("3x3x3") {
    val game = new Game(null, null, null,
      Settings(1, 3, 2, 3),
      GameState(Map(
        Vector(0, 0, 0) -> Piece.whitePeon,
        Vector(0, 0, 2) -> Piece.blackPeon,
        Vector(1, 1, 0) -> Piece.blackKing,
        Vector(0, 1, 2) -> Piece.whiteKing,
        Vector(2, 0, 0) -> Piece.whitePeon,
        Vector(2, 1, 2) -> Piece.blackKing),
        Player.white,
        None))

    setField(game, "isGameOver", false)
    setField(game, "bitmap$0", 3.asInstanceOf[Byte])

    assert(new TerminalView().display(game) ==
      """Board plane (0, x, y)
        |   ╭───┬───╮
        | 2 │ b │ W │
        |   ├───┼───┤
        | 1 │   │   │
        |   ├───┼───┤
        | 0 │ w │   │
        |   ╰───┴───╯
        |     0   1
        |Board plane (1, x, y)
        |   ╭───┬───╮
        | 2 │   │   │
        |   ├───┼───┤
        | 1 │   │   │
        |   ├───┼───┤
        | 0 │   │ B │
        |   ╰───┴───╯
        |     0   1
        |Board plane (2, x, y)
        |   ╭───┬───╮
        | 2 │   │ B │
        |   ├───┼───┤
        | 1 │   │   │
        |   ├───┼───┤
        | 0 │ w │   │
        |   ╰───┴───╯
        |     0   1
        |White to move.
        |""".stripMargin)
  }

  test("8x8") {
    val game = new Game(null, null, null,
      Settings(8, 8, 2),
      GameState(Map(
        Vector(0, 0) -> Piece.whitePeon,
        Vector(0, 4) -> Piece.blackPeon,
        Vector(4, 6) -> Piece.blackKing,
        Vector(7, 5) -> Piece.whiteKing),
        Player.black,
        None))

    setField(game, "isGameOver", true)
    setField(game, "bitmap$0", 3.asInstanceOf[Byte])

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
        |Game over!
        |""".stripMargin)
  }

  test("12x12") {
    val game = new Game(null, null, null,
      Settings(12,12,4),
      GameState(Map(
        Vector(0, 2) -> Piece.blackPeon,
        Vector(8, 4) -> Piece.blackPeon,
        Vector(4, 6) -> Piece.blackKing,
        Vector(9, 9) -> Piece.whitePeon,
        Vector(11,9) -> Piece.whiteKing,
        Vector(7,11) -> Piece.whiteKing),
        Player.black,
        Some(Vector(9,9))))

    setField(game, "isGameOver", false)
    setField(game, "bitmap$0", 3.asInstanceOf[Byte])

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
        |Black to continue jumping from (9, 9) (or pass).
        |""".stripMargin)
  }
}
