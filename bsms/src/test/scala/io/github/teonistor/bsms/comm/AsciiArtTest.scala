package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip}
import io.github.teonistor.bsms.data.ShipInPlay
import org.scalatest.funspec.AnyFunSpec

class AsciiArtTest extends AnyFunSpec {

  describe("illustrateBoard") {

  }

  describe("illustrateGame") {

  }

  describe("illustrateState") {

    it("Empty") {
      assert(AsciiArt.illustrateBoard(Map.empty, 3, 3) ==
        Iterator.continually("          ").take(4).mkString("\n"))
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
