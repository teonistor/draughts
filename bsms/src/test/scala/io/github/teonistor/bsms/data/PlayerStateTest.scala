package io.github.teonistor.bsms.data

import io.github.teonistor.bsms.core.Orientation.horizontal
import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip, mine}
import io.github.teonistor.bsms.rule.ShipPlacementRule
import io.vavr.control.Validation.{invalid, valid}
import org.apache.commons.lang3.RandomUtils.nextInt
import org.mockito.IdiomaticMockito
import org.scalatest.funspec.AnyFunSpec

// noinspection NameBooleanParameters
class PlayerStateTest extends AnyFunSpec with IdiomaticMockito {

  private val board1 = mock[OwnBoard]
  private val board2 = mock[OwnBoard]
  private val ship = mock[ShipDescription]
  private val position = mock[Position]
  private val movement = mock[Vector[Int]]

  describe("base trait PlayerState") {

    val setter = mock[OwnBoard => PlayerState]

    // noinspection NotImplementedCode, TypeAnnotation
    val st = new PlayerState {
      override val board = board1
      override def withBoard(board: OwnBoard) = setter(board)
    }

    it("cannot use ship outside ship placement stage") {
      val result = st.useShip(ship)
      assert(result.isInvalid)
      assert(result.getError == "Cannot use ship outside ship placement stage")
    }

    it("cannot use mine outside mine placement stage") {
      val result = st.useMine()
      assert(result.isInvalid)
      assert(result.getError == "Cannot use mine outside mine placement stage")
    }

    it("cannot move ship outside ship movement stage") {
      val result = st.moveShip(position, movement)
      assert(result.isInvalid)
      assert(result.getError == "Cannot move ship outside ship movement stage")
    }

    it("cannot shoot outside shooting stage") {
      val result = st.shoot()
      assert(result.isInvalid)
      assert(result.getError == "Cannot shoot outside shooting stage")
    }

    it("call setter") {
      val nextState = mock[PlayerState]
      setter(board2) returns nextState

      assert(st.withBoard(board2) == nextState)
    }
  }

  describe("ship placement stage") {

    it("use a ship") {
      val nautilus = ShipDescription("Nautilus", 3)

      val result = PlayerStateShipPlacement(Map.empty, Map.empty, Set(nautilus, ship)).useShip(ship)
      assert(result.isValid)
      assert(result.get.asInstanceOf[PlayerStateShipPlacement].shipsToPlace == Set(nautilus))
    }

    it("cannot use a ship you don't have") {
      val nautilus = ShipDescription("Nautilus", 3)
      val titanic = ShipDescription("Titanic", 5)

      List(ship,
          ShipDescription("Atlantis", 5),
          ShipDescription("Eleanor", 3),
          ShipDescription("Nautilus", 5),
          ShipDescription("Titanic", 3))
        .map(ship => PlayerStateShipPlacement(Map.empty, Map.empty, Set(nautilus, titanic)).useShip(ship))
        .foreach(result => {
          assert(result.isInvalid)
          assert(result.getError == "Cannot use a ship you do not have")
        })
    }

    it("place a ship") {
      withObjectMocked[ShipPlacementRule.type] {

        ShipPlacementRule.placeShip(board1, ship, position, horizontal) returns valid(board2)

        val result = PlayerStateShipPlacement(board1, Map.empty, Set.empty).placeShip(ship, position, horizontal)
        assert(result.isValid)
        assert(result.get.board == board2)
      }
    }

    it("cannot place ship") {
      withObjectMocked[ShipPlacementRule.type] {

        ShipPlacementRule.placeShip(board1, ship, position, horizontal) returns invalid("Some reason")

        val result = PlayerStateShipPlacement(board1, Map.empty, Set.empty).placeShip(ship, position, horizontal)
        assert(result.isInvalid)
        assert(result.getError == "Some reason")
      }
    }
  }

  describe("mine placement stage") {

    it("place mine in water") {
      val result = PlayerStateMinePlacement(Map.empty, Map.empty, 0).placeMine(Vector(7, 5))
      assert(result.board == Map(Vector(7, 5) -> Left(mine)))
    }

    it("hit ship") {
      val shipBefore = ShipInPlay("Fishing Boat", Map(Vector(2, 3) -> healthyShip, Vector(2, 4) -> healthyShip))
      val shipAfter = ShipInPlay("Fishing Boat", Map(Vector(2, 3) -> healthyShip, Vector(2, 4) -> damagedShip))

      val result = PlayerStateMinePlacement(Map(Vector(2, 3) -> Right(shipBefore), Vector(2, 4) -> Right(shipBefore)), Map.empty, 0)
        .placeMine(Vector(2, 4))
      assert(result.board == Map(Vector(2, 3) -> Right(shipAfter), Vector(2, 4) -> Right(shipAfter)))
    }

    it("use a mine") {
      val mines = nextInt(1, 100)

      val result = PlayerStateMinePlacement(Map.empty, Map.empty, mines + 1).useMine()
      assert(result.isValid)
      assert(result.get.asInstanceOf[PlayerStateMinePlacement].minesToPlace == mines)
    }

    it("cannot use a mine you don't have") {
      val result = PlayerStateMinePlacement(Map.empty, Map.empty, 0).useMine()
      assert(result.isInvalid)
      assert(result.getError == "Cannot use a mine you do not have")
    }
  }

  describe("ship movement stage") {

    it("move a ship") {
      withObjectMocked[ShipPlacementRule.type] {

        ShipPlacementRule.moveShip(board1, position, movement) returns valid(board2)

        val result = PlayerStateMovement(board1, Map.empty, true).moveShip(position, movement)
        assert(result.isValid)
        assert(result.get.board == board2)
      }
    }

    it("cannot move because move used") {
      val result = PlayerStateMovement(board1, Map.empty, false).moveShip(position, movement)
      assert(result.isInvalid)
      assert(result.getError == "You do not have a move available")
    }

    it("cannot move ship due to rule") {
      withObjectMocked[ShipPlacementRule.type] {

        ShipPlacementRule.moveShip(board1, position, movement) returns invalid("Some other reason")

        val result = PlayerStateMovement(board1, Map.empty, true).moveShip(position, movement)
        assert(result.isInvalid)
        assert(result.getError == "Some other reason")
      }
    }
  }

  describe("shooting stage") {

    it("shoot a shot") {
      val result = PlayerStateShooting(Map.empty, Map.empty, true).shoot()
      assert(result.isValid)
      assert(!result.get.asInstanceOf[PlayerStateShooting].shotToShoot)
    }

    it("cannot shoot a shot you don't have") {
      val result = PlayerStateShooting(Map.empty, Map.empty, false).shoot()
      assert(result.isInvalid)
      assert(result.getError == "You do not have a shot available")
    }
  }
}
