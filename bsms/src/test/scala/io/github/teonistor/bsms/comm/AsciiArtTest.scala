package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.core._
import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip}
import io.github.teonistor.bsms.data._
import org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric
import org.mockito.ArgumentMatchers.any
import org.mockito.IdiomaticMockito
import org.scalactic.source.{Position => Pos}
import org.scalatest.funspec.AnyFunSpec

import scala.util.Random.nextInt

class AsciiArtTest extends AnyFunSpec with IdiomaticMockito {

  describe("illustrateGame") {
    val aliceState = mock[PlayerState]
    val bobState = mock[PlayerState]
    val aliceCursor = mock[Option[Position]]
    val bobCursor = mock[Option[Position]]
    val width = nextInt(99)
    val height = nextInt(99)
    val game = BattleshipMinesweeper(GameSettings(Set.empty, 0, width, height), aliceState, bobState)

    it("works") {
      withObjectMocked[AsciiArt.type] {
        AsciiArt.illustrateGame(any, any, any) shouldCall realMethod
        AsciiArt.illustrateState(aliceState, aliceCursor, width, height) returns
          """        X
            |       XXX
            |      XX XX
            |     XX   XX
            |    XX     XX
            |   XXXXXXXXXXX
            |  XX         XX
            | XX           XX
            |XX             XX
            |
            |≈≈≈≈≈  ≈≈≈
            |≈≈ ≈≈≈≈≈≈≈≈≈≈≈""".stripMargin
        AsciiArt.illustrateState(bobState, bobCursor, width, height) returns
          """XXXXXXXXXXXXX
            |XX          XXX
            |XX           XX
            |XX          XXX
            |XXXXXXXXXXXXX
            |XX          XXX
            |XX           XX
            |XX          XXX
            |XXXXXXXXXXXXX
            |
            |≈≈≈ ≈≈≈≈≈≈≈≈≈ ≈≈≈≈""".stripMargin

        assert(AsciiArt.illustrateGame(game, aliceCursor, bobCursor)==
          """                           ║║
            |             X             ║║     XXXXXXXXXXXXX
            |            XXX            ║║     XX          XXX
            |           XX XX           ║║     XX           XX
            |          XX   XX          ║║     XX          XXX
            |         XX     XX         ║║     XXXXXXXXXXXXX
            |        XXXXXXXXXXX        ║║     XX          XXX
            |       XX         XX       ║║     XX           XX
            |      XX           XX      ║║     XX          XXX
            |     XX             XX     ║║     XXXXXXXXXXXXX
            |                           ║║
            |     ≈≈≈≈≈  ≈≈≈            ║║     ≈≈≈ ≈≈≈≈≈≈≈≈≈ ≈≈≈≈
            |     ≈≈ ≈≈≈≈≈≈≈≈≈≈≈        ║║
            |                           ║║""".stripMargin)

        AsciiArt.illustrateState(any, any, any, any) wasCalled twice
      }
    }
  }

  // noinspection NameBooleanParameters
  describe("illustrateState") {
    val board = mock[OwnBoard]
    val cursor = mock[Option[Position]]
    val width = nextInt(99)
    val height = nextInt(99)
    val boardStr = randomAlphanumeric(5) + "\n" + randomAlphanumeric(5) + "\n" + randomAlphanumeric(5)

    def customTest(name: String)(assertion: => Any)(implicit pos: Pos): Unit = {
      it(name) {
        withObjectMocked[AsciiArt.type] {
          AsciiArt.illustrateBoard(board, cursor, width, height) returns boardStr
          AsciiArt.illustrateState(any, cursor, any, any) shouldCall realMethod

          assertion

          AsciiArt.illustrateBoard(board, cursor, width, height) wasCalled once
        }
      }(pos)
    }

    customTest("illustrate ship placement in progress") {
      assert(AsciiArt.illustrateState(PlayerStateShipPlacement(board, Map.empty, Set(ShipDescription("Fishing Boat", 4))), cursor, width, height) ==
        boardStr + "\nShip placement:\n> Fishing Boat (length 4)")
    }

    customTest("illustrate ship placement complete") {
      assert(AsciiArt.illustrateState(PlayerStateShipPlacement(board, Map.empty, Set.empty), cursor, width, height) ==
        boardStr + "\nShip placement complete. Waiting for other player")
    }

    customTest("illustrate mine placement in progress") {
      assert(AsciiArt.illustrateState(PlayerStateMinePlacement(board, Map.empty, 3), cursor, width, height) ==
        boardStr + "\nMines to place: 3")
    }

    customTest("illustrate mine placement complete") {
      assert(AsciiArt.illustrateState(PlayerStateMinePlacement(board, Map.empty, 0), cursor, width, height) ==
        boardStr + "\nMine placement complete. Waiting for other player")
    }

    customTest("illustrate movement available") {
      assert(AsciiArt.illustrateState(PlayerStateMovement(board, Map.empty, true), cursor, width, height) ==
        boardStr + "\nYou may move")
    }

    customTest("illustrate movement unavailable") {
      assert(AsciiArt.illustrateState(PlayerStateMovement(board, Map.empty, false), cursor, width, height) ==
        boardStr + "\nYou have moved. Waiting for other player")
    }

    customTest("illustrate shot available") {
      assert(AsciiArt.illustrateState(PlayerStateShooting(board, Map.empty, true), cursor, width, height) ==
        boardStr + "\nPick a target to shoot")
    }

    customTest("illustrate shot unavailable") {
      assert(AsciiArt.illustrateState(PlayerStateShooting(board, Map.empty, false), cursor, width, height) ==
        boardStr + "\nShot fired. Waiting for other player")
    }
  }

  describe("illustrateBoard") {

    it("Empty grid") {
      assert(AsciiArt.illustrateBoard(Map.empty, None, 3, 3) ==
        """╭──┬──┬──╮
          |├──┼──┼──┤
          |├──┼──┼──┤
          |╰──┴──┴──╯""".stripMargin)
    }

    it("Empty grid and a cursor") {
      assert(AsciiArt.illustrateBoard(Map.empty, Some(Vector(3,2)), 4, 4) ==
        """╭──┬──┬──┬──╮
          |├──┼──┼──┼──┤
          |├──┼──┼──┼╲╱┤
          |├──┼──┼──┼──┤
          |╰──┴──┴──┴──╯""".stripMargin)
      assert(AsciiArt.illustrateBoard(Map.empty, Some(Vector(2,1)), 5, 3) ==
        """╭──┬──┬──┬──┬──╮
          |├──┼──┼╲╱┼──┼──┤
          |├──┼──┼──┼──┼──┤
          |╰──┴──┴──┴──┴──╯""".stripMargin)
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

      assert(AsciiArt.illustrateBoard(board, None, 7, 9) ==
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

    it("Damaged ships and a cursor") {
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
        Vector(4,3) -> healthyShip,
        Vector(4,4) -> healthyShip,
        Vector(4,5) -> damagedShip,
        Vector(4,6) -> healthyShip,
        Vector(4,7) -> healthyShip))
      val redBoat = ShipInPlay("Red Boat", Map(
        Vector(5,1) -> healthyShip,
        Vector(6,1) -> damagedShip))
      val whiteBoat = ShipInPlay("White Boat", Map(
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

      assert(AsciiArt.illustrateBoard(board, Some(Vector(3,2)), 7, 9) ==
        """╭───██──────╮
          |╰──┬───██───┴──┬───██╮
          |╭──┼──────╲╱───┴─────╯
          |│  │        ╭──╮
          |│██│        │  │
          |╰──╯     ╭──┤██│
          |         │  │  │
          |         │██│  │
          |         ╰──┴──╯
          |""".stripMargin)
    }
  }
}
