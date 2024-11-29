package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.core.{PlayerStateMinePlacement, PlayerStateMovement, PlayerStateShipPlacement, PlayerStateShooting}
import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip}
import io.github.teonistor.bsms.data._
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.mockito.ArgumentMatchers.any
import org.mockito.IdiomaticMockito
import org.scalactic.source.Position
import org.scalatest.funspec.AnyFunSpec

import scala.util.Random.nextInt

class AsciiArtTest extends AnyFunSpec with IdiomaticMockito {

  describe("illustrateGame") {

  }

  // noinspection NameBooleanParameters
  describe("illustrateState") {
    val board = mock[OwnBoard]
    val width = nextInt(99)
    val height = nextInt(99)
    val boardStr = randomAlphanumeric(5) + "\n" + randomAlphanumeric(5) + "\n" + randomAlphanumeric(5)

    def customTest(name: String)(assertion: => Any)(implicit pos: Position): Unit = {
      it(name) {
        withObjectMocked[AsciiArt.type] {
          AsciiArt.illustrateBoard(board, width, height) returns boardStr
          AsciiArt.illustrateState(any(), any(), any()) shouldCall realMethod

          assertion

          AsciiArt.illustrateBoard(board, width, height) wasCalled once
        }
      }(pos)
    }

    customTest("illustrate ship placement in progress") {
      assert(AsciiArt.illustrateState(
        PlayerStateShipPlacement(board, Map.empty, Set(ShipDescription("Fishing Boat", 4))), width, height) ==
        boardStr + "\nShip placement:\n> Fishing Boat (length 4)")
    }

    customTest("illustrate ship placement complete") {
      assert(AsciiArt.illustrateState(
        PlayerStateShipPlacement(board, Map.empty, Set.empty), width, height) ==
        boardStr + "\nShip placement complete. Waiting for other player")
    }

    customTest("illustrate mine placement in progress") {
      assert(AsciiArt.illustrateState(
        PlayerStateMinePlacement(board, Map.empty, 3), width, height) ==
        boardStr + "\nMines to place: 3")
    }

    customTest("illustrate mine placement complete") {
      assert(AsciiArt.illustrateState(
        PlayerStateMinePlacement(board, Map.empty, 0), width, height) ==
        boardStr + "\nMine placement complete. Waiting for other player")
    }

    customTest("illustrate movement available") {
      assert(AsciiArt.illustrateState(PlayerStateMovement(board, Map.empty, true), width, height) ==
        boardStr + "\nYou may move")
    }

    customTest("illustrate movement unavailable") {
      assert(AsciiArt.illustrateState(PlayerStateMovement(board, Map.empty, false), width, height) ==
        boardStr + "\nYou have moved. Waiting for other player")
    }

    customTest("illustrate shot available") {
      assert(AsciiArt.illustrateState(PlayerStateShooting(board, Map.empty, true), width, height) ==
        boardStr + "\nPick a target to shoot")
    }

    customTest("illustrate shot unavailable") {
      assert(AsciiArt.illustrateState(PlayerStateShooting(board, Map.empty, false), width, height) ==
        boardStr + "\nShot fired. Waiting for other player")
    }
  }

  describe("illustrateBoard") {

    it("Empty") {
      assert(AsciiArt.illustrateBoard(Map.empty, 3, 3) == "\n\n\n")
    }

    it("Healthy ships") {
      val blueBoat = ShipInPlay("Blue Boat", Map(
        Vector(0,0) -> healthyShip,
        Vector(1,0) -> healthyShip,
        Vector(2,0) -> healthyShip,
        Vector(3,0) -> healthyShip))
      val greenBoat = ShipInPlay("Green Boat", Map(
        Vector(1,1) -> healthyShip,
        Vector(2,1) -> healthyShip,
        Vector(3,1) -> healthyShip,
        Vector(4,1) -> healthyShip))
      val purpleBoat = ShipInPlay("Purple Boat", Map(
        Vector(4,5) -> healthyShip,
        Vector(4,6) -> healthyShip,
        Vector(4,7) -> healthyShip))
      val redBoat = ShipInPlay("Red Boat", Map(
        Vector(5,1) -> healthyShip,
        Vector(6,1) -> healthyShip))
      val whiteBoat = ShipInPlay("White Boat", Map(
        Vector(3,3) -> healthyShip,
        Vector(3,4) -> healthyShip,
        Vector(3,5) -> healthyShip,
        Vector(3,6) -> healthyShip,
        Vector(3,7) -> healthyShip))
      val yellowBoat = ShipInPlay("Yellow Boat", Map(
        Vector(0,2) -> healthyShip,
        Vector(0,3) -> healthyShip,
        Vector(0,4) -> healthyShip))
      val board = Iterator(blueBoat, greenBoat, purpleBoat, redBoat, whiteBoat, yellowBoat)
        .flatMap(ship => ship.parts.keys.map((_, Right(ship))))
        .toMap

      assert(AsciiArt.illustrateBoard(board, 7, 9) ==
        """╭───────────╮
          |╰──┬────────┴──┬─────╮
          |╭──┼───────────┴─────╯
          |│  │     ╭──╮
          |│  │     │  │
          |╰──╯     │  ├──╮
          |         │  │  │
          |         │  │  │
          |         ╰──┴──╯
          |""".stripMargin)

      // Nicer but let's set aside for now
      //      assert(AsciiArt.illustrateBoard(Map.empty, 3, 3) ==
      //        """╭──────────╮
      //          |╰──╭───────┴──┬─────╮
      //          |╭──┼──────────┴─────╯
      //          |│  │    ╭──╮
      //          |│  │    │  │
      //          |╰──╯    │  │──╮
      //          |        │  │  │
      //          |        │  │  │
      //          |        ╰──╯──╯
      //          |
      //          |
      //          |""".stripMargin)
    }

    it("Damaged ships") {
      val blueBoat = ShipInPlay("Blue Boat", Map(
        Vector(0,0) -> healthyShip,
        Vector(1,0) -> damagedShip,
        Vector(2,0) -> healthyShip,
        Vector(3,0) -> healthyShip))
      val greenBoat = ShipInPlay("Green Boat", Map(
        Vector(1,1) -> healthyShip,
        Vector(2,1) -> damagedShip,
        Vector(3,1) -> healthyShip,
        Vector(4,1) -> healthyShip))
      val purpleBoat = ShipInPlay("Purple Boat", Map(
        Vector(4,5) -> damagedShip,
        Vector(4,6) -> healthyShip,
        Vector(4,7) -> healthyShip))
      val redBoat = ShipInPlay("Red Boat", Map(
        Vector(5,1) -> healthyShip,
        Vector(6,1) -> damagedShip))
      val whiteBoat = ShipInPlay("White Boat", Map(
        Vector(3,3) -> healthyShip,
        Vector(3,4) -> healthyShip,
        Vector(3,5) -> healthyShip,
        Vector(3,6) -> healthyShip,
        Vector(3,7) -> damagedShip))
      val yellowBoat = ShipInPlay("Yellow Boat", Map(
        Vector(0,2) -> healthyShip,
        Vector(0,3) -> healthyShip,
        Vector(0,4) -> damagedShip))
      val board = Iterator(blueBoat, greenBoat, purpleBoat, redBoat, whiteBoat, yellowBoat)
        .flatMap(ship => ship.parts.keys.map((_, Right(ship))))
        .toMap

      assert(AsciiArt.illustrateBoard(board, 7, 9) ==
        """╭───██──────╮
          |╰──┬───██───┴──┬───██╮
          |╭──┼───────────┴─────╯
          |│  │     ╭──╮
          |│██│     │  │
          |╰──╯     │  ├██╮
          |         │  │  │
          |         │██│  │
          |         ╰──┴──╯
          |""".stripMargin)
    }
  }
}
