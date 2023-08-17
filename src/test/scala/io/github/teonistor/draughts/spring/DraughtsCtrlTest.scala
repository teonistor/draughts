package io.github.teonistor.draughts.spring

import io.github.teonistor.draughts.data.GameState
import io.github.teonistor.draughts.{Game, Piece, Player}
import io.vavr.control.Validation.{invalid, valid}
import org.mockito.BDDMockito.`given`
import org.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuiteLike
import org.springframework.messaging.simp.SimpMessagingTemplate

class DraughtsCtrlTest extends AnyFunSuiteLike with MockitoSugar {

  test("Send game when no ongoing jump, not over, 4D-ish") {
    val ws = mock[SimpMessagingTemplate]
    val ctrl = new DraughtsCtrl(ws, null)

    val game = mock[Game]
    given(game.gameState).willReturn(GameState(
      Map(Vector(4,3,2,1) -> Piece.blackKing, Vector(9,8,7,6,5) -> Piece.whiteKing),
      Player.white,
      None))
    given(game.availableMoves).willReturn(Map(
      Vector(1,2,3,4) -> Map(Vector(5,6,7,8) -> valid(null), Vector(6,7,8,9) -> valid(null)),
      Vector(2,3,4,5) -> Map(Vector(6,7,8,9) -> valid(null), Vector(7,8,9,0) -> invalid("Busted"))))
    given(game.isGameOver) willReturn false

    ctrl.display(game)

    val expected = ctrl.SendableState(
      Map("" -> Map(
        "0,4" -> Map("3,2,1" -> Piece.blackKing),
        "9,8" -> Map("7,6,5" -> Piece.whiteKing))),
      Player.white,
      Map("" -> Map(
        "0,1" -> Map("2,3,4" -> Map("" -> Map("0,5" -> Map("6,7,8" -> true), "0,6" -> Map("7,8,9" -> true)))),
        "0,2" -> Map("3,4,5" -> Map("" -> Map("0,6" -> Map("7,8,9" -> true)))))),
      "White to move.")
    verify(ws).convertAndSend("/draughts/draughts-state", expected)
  }

  test("Send game when ongoing jump exists, not over, 6D-ish") {
    val ws = mock[SimpMessagingTemplate]
    val ctrl = new DraughtsCtrl(ws, null)

    val game = mock[Game]
    given(game.gameState).willReturn(GameState(
      Map(Vector(6, 5, 4, 3, 2, 1) -> Piece.blackKing, Vector(6, 5, 4, 1, 2, 3) -> Piece.whiteKing, Vector(6, 7, 8, 1, 2, 3) -> Piece.whitePeon),
      Player.black,
      Some(Vector(2,7,6,4,9))))
    given(game.availableMoves).willReturn(Map(
      Vector(1, 2, 3, 4, 5, 6) -> Map(Vector(2, 3, 5, 6, 7, 8) -> valid(null), Vector(2,3,5,6,7,9) -> valid(null)),
      Vector(1, 2, 3, 5, 6, 7) -> Map(Vector(2, 3, 5, 6, 8, 9) -> valid(null))))
    given(game.isGameOver) willReturn false

    ctrl.display(game)

    val expected = ctrl.SendableState(
      Map("6" -> Map(
        "5,4" -> Map("3,2,1" -> Piece.blackKing, "1,2,3" -> Piece.whiteKing),
        "7,8" -> Map("1,2,3" -> Piece.whitePeon))),
      Player.black,
      Map("1" -> Map("2,3" -> Map(
        "4,5,6" -> Map("2" -> Map("3,5" -> Map("6,7,8" -> true, "6,7,9" -> true))),
        "5,6,7" -> Map("2" -> Map("3,5" -> Map("6,8,9" -> true)))))),
      "Black to continue jumping from (2, 7, 6, 4, 9) (or pass).")
    verify(ws).convertAndSend("/draughts/draughts-state", expected)
  }

  test("Send game when over") {
    val ws = mock[SimpMessagingTemplate]
    val ctrl = new DraughtsCtrl(ws, null)

    val game = mock[Game]
    given(game.gameState) willReturn GameState(
      Map.empty,
      Player.white,
      None)
    given(game.availableMoves) willReturn Map.empty
    given(game.isGameOver) willReturn true

    ctrl.display(game)

    val expected = ctrl.SendableState(
      Map.empty,
      Player.white,
      Map.empty,
      "Game over!")
    verify(ws).convertAndSend("/draughts/draughts-state", expected)
  }

  test("Send settings with 4 dimensions") {
    assert(false)
  }
}
