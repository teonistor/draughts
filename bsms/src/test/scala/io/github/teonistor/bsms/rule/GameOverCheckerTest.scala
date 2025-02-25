package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.core.{BattleshipMinesweeper, PlayerState, PlayerStateMovement, PlayerStateShooting}
import io.github.teonistor.bsms.data.GameCondition._
import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip, mine}
import io.github.teonistor.bsms.data.{GameSettings, OwnBoard, Position, ShipInPlay}
import org.mockito.IdiomaticMockito
import org.scalatest.funspec.AnyFunSpec

class GameOverCheckerTest extends AnyFunSpec with IdiomaticMockito {

  private val settings = mock[GameSettings]

  describe("Non-terminable stages") {
    it("Game continues") {
      val someState = mock[PlayerState]
      assert(GameOverChecker.check(BattleshipMinesweeper(
        settings, someState, someState)) == continues)
    }
  }

  describe("Terminable stages") {
    val pos1 = mock[Position]
    val pos2 = mock[Position]
    val pos3 = mock[Position]
    val pos4 = mock[Position]
    val undamagedShip = ShipInPlay("Duke", Map(pos2 -> healthyShip, pos3 -> healthyShip))
    val deadShip = ShipInPlay("Duck", Map(pos3 -> damagedShip, pos4 -> damagedShip))
    val aliveBoard = Map(pos1 -> Left(mine), pos2 -> Right(undamagedShip), pos4 -> Right(deadShip))
    val deadBoard = Map(pos1 -> Right(deadShip), pos2 -> Right(deadShip), pos3 -> Left(mine))

    val stateCombinator = {
      val stateMakers = List(
        PlayerStateMovement(_: OwnBoard, Map.empty, true),
        PlayerStateShooting(_: OwnBoard, Map.empty, true))
      stateMakers.flatMap(aliceState => stateMakers.map(bobState => (aliceState, bobState)))
        .zipWithIndex
        .map { case ((a, b), i) => (a, b, (i + 1).toString) }
    }

    describe("Game continues") {
      stateCombinator.foreach { case (a, b, s) =>
        it(s) {
          assert(GameOverChecker.check(BattleshipMinesweeper(settings, a(aliveBoard), b(aliveBoard))) == continues)
        }
      }
    }

    describe("Alice wins") {
      stateCombinator.foreach { case (a, b, s) =>
        it(s) {
          assert(GameOverChecker.check(BattleshipMinesweeper(settings, a(aliveBoard), b(deadBoard))) == aliceWins)
        }
      }
    }

    describe("Bob wins") {
      stateCombinator.foreach { case (a, b, s) =>
        it(s) {
          assert(GameOverChecker.check(BattleshipMinesweeper(settings, a(deadBoard), b(aliveBoard))) == bobWins)
        }
      }
    }

    describe("Everyone loses") {
      stateCombinator.foreach { case (a, b, s) =>
        it(s) {
          assert(GameOverChecker.check(BattleshipMinesweeper(settings, a(deadBoard), b(deadBoard))) == everyoneLoses)
        }
      }
    }
  }
}
