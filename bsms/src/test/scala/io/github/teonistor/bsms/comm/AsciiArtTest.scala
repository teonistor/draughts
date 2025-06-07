package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.core._
import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip, mine}
import io.github.teonistor.bsms.data._
import org.mockito.ArgumentMatchers.any
import org.mockito.IdiomaticMockito
import org.scalatest.funspec.AnyFunSpec

import scala.util.Random.nextInt

class AsciiArtTest extends AnyFunSpec with IdiomaticMockito {

  describe("illustrateGame") {
    val aliceState = mock[PlayerState]
    val bobState = mock[PlayerState]
    val aliceBoard = mock[OwnBoard]
    val bobBoard = mock[OwnBoard]

    val aliceCursor = mock[Cursor]
    val bobCursor = mock[Cursor]
    val aliceOwnCursor = mock[Option[Position]]
    val bobOwnCursor = mock[Option[Position]]
    val aliceOpponentCursor = mock[Option[Position]]
    val bobOpponentCursor = mock[Option[Position]]
    val aliceOverlay = mock[Option[OwnBoard]]
    val bobOverlay = mock[Option[OwnBoard]]

    val width = nextInt(99)
    val height = nextInt(99)

    it("works") {
      aliceState .board          returns aliceBoard
      bobState   .board          returns bobBoard
      aliceCursor.ownCursor      returns aliceOwnCursor
      bobCursor  .ownCursor      returns bobOwnCursor
      aliceCursor.opponentCursor returns aliceOpponentCursor
      bobCursor  .opponentCursor returns bobOpponentCursor
      withObjectMocked[AsciiArt.type] {
        AsciiArt.illustrateGame(any, any, any) shouldCall realMethod
        AsciiArt.illustrateMines(bobBoard, aliceOpponentCursor, width, height) returns
          """ ~~              ~~
            | ~~ ~~        ~~ ~~
            | ~~    ~~  ~~    ~~
            | ~~      ~~      ~~
            | ~~      ~~      ~~
            | ~~              ~~
            | ~~              ~~
            | ~~              ~~
            | ~~              ~~""".stripMargin
        AsciiArt.illustrateShips(aliceBoard, aliceOverlay, aliceOwnCursor, width, height) returns
          """        X
            |       XXX
            |      XX XX
            |     XX   XX
            |    XX     XX
            |   XXXXXXXXXXX
            |  XX         XX
            | XX           XX
            |XX             XX""".stripMargin
        AsciiArt.illustrateInfo(aliceState) returns
          """≈≈≈≈≈  ≈≈≈
            |≈≈ ≈≈≈≈≈≈≈≈≈≈≈""".stripMargin
        AsciiArt.illustrateMines(aliceBoard, bobOpponentCursor, width, height) returns
          """ ⋅⋅⋅      ⋅⋅⋅
            | ⋅⋅ ⋅⋅  ⋅⋅ ⋅⋅
            | ⋅⋅   ⋅⋅   ⋅⋅
            | ⋅⋅   ⋅⋅   ⋅⋅
            | ⋅⋅        ⋅⋅
            | ⋅⋅        ⋅⋅
            | ⋅⋅        ⋅⋅
            | ⋅⋅        ⋅⋅""".stripMargin
        AsciiArt.illustrateShips(bobBoard, bobOverlay, bobOwnCursor, width, height) returns
          """XXXXXXXXXXXXX
            |XX          XXX
            |XX           XX
            |XX          XXX
            |XXXXXXXXXXXXX
            |XX          XXX
            |XX           XX
            |XX          XXX
            |XXXXXXXXXXXXX""".stripMargin
        AsciiArt.illustrateInfo(bobState) returns "≈≈≈ ≈≈≈≈≈≈≈≈≈ ≈≈≈≈"

        assert(AsciiArt.illustrateGame(AsciiDisplayablePlayerState(aliceState, aliceCursor, aliceOverlay),
                                       AsciiDisplayablePlayerState(bobState, bobCursor, bobOverlay),
                                       GameSettings(Set.empty, 0, width, height)) ==
          """                             ║║
            |      ~~              ~~     ║║      ⋅⋅⋅      ⋅⋅⋅
            |      ~~ ~~        ~~ ~~     ║║      ⋅⋅ ⋅⋅  ⋅⋅ ⋅⋅
            |      ~~    ~~  ~~    ~~     ║║      ⋅⋅   ⋅⋅   ⋅⋅
            |      ~~      ~~      ~~     ║║      ⋅⋅   ⋅⋅   ⋅⋅
            |      ~~      ~~      ~~     ║║      ⋅⋅        ⋅⋅
            |      ~~              ~~     ║║      ⋅⋅        ⋅⋅
            |      ~~              ~~     ║║      ⋅⋅        ⋅⋅
            |      ~~              ~~     ║║      ⋅⋅        ⋅⋅
            |      ~~              ~~     ║║
            |                             ║║     XXXXXXXXXXXXX
            |             X               ║║     XX          XXX
            |            XXX              ║║     XX           XX
            |           XX XX             ║║     XX          XXX
            |          XX   XX            ║║     XXXXXXXXXXXXX
            |         XX     XX           ║║     XX          XXX
            |        XXXXXXXXXXX          ║║     XX           XX
            |       XX         XX         ║║     XX          XXX
            |      XX           XX        ║║     XXXXXXXXXXXXX
            |     XX             XX       ║║
            |                             ║║     ≈≈≈ ≈≈≈≈≈≈≈≈≈ ≈≈≈≈
            |     ≈≈≈≈≈  ≈≈≈              ║║
            |     ≈≈ ≈≈≈≈≈≈≈≈≈≈≈          ║║
            |                             ║║""".stripMargin)

        AsciiArt.illustrateMines(any, any, any, any) wasCalled twice
        AsciiArt.illustrateShips(any, any, any, any, any) wasCalled twice
        AsciiArt.illustrateInfo(any) wasCalled twice
      }
    }
  }

  describe("illustrateMines") {

    it("Empty grid") {
      assert(AsciiArt.illustrateMines(Map.empty, None, 3, 3) ==
        """╭──┬──┬──╮
          |├──┼──┼──┤
          |├──┼──┼──┤
          |╰──┴──┴──╯""".stripMargin)
    }

    it("Empty grid and a cursor") {
      assert(AsciiArt.illustrateMines(Map.empty, Some(Vector(3, 2)), 4, 4) ==
        """╭──┬──┬──┬──╮
          |├──┼──┼──┼──┤
          |├──┼──┼──┼╲╱┤
          |├──┼──┼──┼──┤
          |╰──┴──┴──┴──╯""".stripMargin)
      assert(AsciiArt.illustrateMines(Map.empty, Some(Vector(2, 1)), 5, 3) ==
        """╭──┬──┬──┬──┬──╮
          |├──┼──┼╲╱┼──┼──┤
          |├──┼──┼──┼──┼──┤
          |╰──┴──┴──┴──┴──╯""".stripMargin)
    }

    it("Mines") {
      assert(AsciiArt.illustrateMines(Map(Vector(1, 1) -> Left(mine)), Some(Vector(3, 2)), 4, 4) ==
        """╭──┬──┬──┬──╮
          |├──┼MM┼──┼──┤
          |├──┼──┼──┼╲╱┤
          |├──┼──┼──┼──┤
          |╰──┴──┴──┴──╯""".stripMargin)
      assert(AsciiArt.illustrateMines(Map(Vector(2, 0) -> Left(mine), Vector(0, 1) -> Left(mine), Vector(3, 2) -> Left(mine)), None, 5, 3) ==
        """╭──┬──┬MM┬──┬──╮
          |├MM┼──┼──┼──┼──┤
          |├──┼──┼──┼MM┼──┤
          |╰──┴──┴──┴──┴──╯""".stripMargin)
    }
  }

  describe("illustrateShips") {

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

      assert(AsciiArt.illustrateShips(board, None, None, 7, 9) ==
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
        Vector(0,2) -> damagedShip,
        Vector(0,3) -> healthyShip,
        Vector(0,4) -> damagedShip))
      val floatingBoat = ShipInPlay("Floating Boat", Map(
        Vector(0,3) -> healthyShip,
        Vector(0,4) -> healthyShip,
        Vector(0,5) -> damagedShip))
      val board = Iterator(blueBoat, greenBoat, purpleBoat, redBoat, whiteBoat, yellowBoat)
        .flatMap(ship => ship.parts.keys.map((_, Right(ship))))
        .toMap
      val overlay = floatingBoat.parts.keys.map((_, Right(floatingBoat)))
        .toMap

      assert(AsciiArt.illustrateShips(board, Some(overlay), Some(Vector(3, 2)), 7, 9) ==
        """╭───██──────╮
          |╰──┬───██───┴──┬───██╮
          |╭██┼──────╲╱───┴─────╯
          |╭──╮        ╭──╮
          |│██│        │  │
          |│██│     ╭──┤██│
          |╰──╯     │  │  │
          |         │██│  │
          |         ╰──┴──╯
          |""".stripMargin)
    }
  }

  // noinspection NameBooleanParameters
  describe("illustrateInfo") {
    val board = mock[OwnBoard]

    it("illustrate ship placement in progress") {
      assert(AsciiArt.illustrateInfo(PlayerStateShipPlacement(board, Map.empty, Set(ShipDescription("Fishing Boat", 4)))) ==
        "Ship placement:\n> Fishing Boat (length 4)")
    }

    it("illustrate ship placement complete") {
      assert(AsciiArt.illustrateInfo(PlayerStateShipPlacement(board, Map.empty, Set.empty)) ==
        "Ship placement complete. Waiting for other player")
    }

    it("illustrate mine placement in progress") {
      assert(AsciiArt.illustrateInfo(PlayerStateMinePlacement(board, Map.empty, 3)) ==
        "Mines to place: 3")
    }

    it("illustrate mine placement complete") {
      assert(AsciiArt.illustrateInfo(PlayerStateMinePlacement(board, Map.empty, 0)) ==
        "Mine placement complete. Waiting for other player")
    }

    it("illustrate movement available") {
      assert(AsciiArt.illustrateInfo(PlayerStateMovement(board, Map.empty, true)) ==
        "You may move")
    }

    it("illustrate movement unavailable") {
      assert(AsciiArt.illustrateInfo(PlayerStateMovement(board, Map.empty, false)) ==
        "You have moved. Waiting for other player")
    }

    it("illustrate shot available") {
      assert(AsciiArt.illustrateInfo(PlayerStateShooting(board, Map.empty, true)) ==
        "Pick a target to shoot")
    }

    it("illustrate shot unavailable") {
      assert(AsciiArt.illustrateInfo(PlayerStateShooting(board, Map.empty, false)) ==
        "Shot fired. Waiting for other player")
    }
  }
}
