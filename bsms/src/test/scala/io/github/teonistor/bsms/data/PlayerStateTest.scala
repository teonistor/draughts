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
      override def isStageOver = ???
      override def nextStage(gameSettings: GameSettings) = ???
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
        .foreach(ship => {
          val initial = PlayerStateShipPlacement(Map.empty, Map.empty, Set(nautilus, titanic))
          val result = initial.useShip(ship)

          assert(!initial.isStageOver)
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

    it("stage over") {
      assert(PlayerStateShipPlacement(board1, Map.empty, Set.empty).isStageOver)
    }

    it("next stage") {
      val settings = mock[GameSettings]
      val board = mock[OwnBoard]
      val minesToPlace = nextInt(2,7)
      settings.minesToPlace returns minesToPlace

      val result = PlayerStateShipPlacement(board, Map.empty, Set.empty).nextStage(settings)
      assert(result == PlayerStateMinePlacement(board, Map.empty, minesToPlace))
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
      val initial = PlayerStateMinePlacement(Map.empty, Map.empty, mines + 1)
      val result = initial.useMine()

      assert(!initial.isStageOver)
      assert(result.isValid)
      assert(result.get.asInstanceOf[PlayerStateMinePlacement].minesToPlace == mines)
    }

    it("cannot use a mine you don't have") {
      val initial = PlayerStateMinePlacement(Map.empty, Map.empty, 0)
      val result = initial.useMine()

      assert(initial.isStageOver)
      assert(result.isInvalid)
      assert(result.getError == "Cannot use a mine you do not have")
    }

    it("next stage") {
      val settings = mock[GameSettings]
      val board = mock[OwnBoard]

      val result = PlayerStateMinePlacement(board, Map.empty, 1).nextStage(settings)
      assert(result == PlayerStateMovement(board, Map.empty, true))
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
      val initial = PlayerStateMovement(board1, Map.empty, false)
      val result = initial.moveShip(position, movement)

      assert(initial.isStageOver)
      assert(result.isInvalid)
      assert(result.getError == "You do not have a move available")
    }

    it("cannot move ship due to rule") {
      val initial = PlayerStateMovement(board1, Map.empty, true)
      withObjectMocked[ShipPlacementRule.type] {

        ShipPlacementRule.moveShip(board1, position, movement) returns invalid("Some other reason")

        val result = initial.moveShip(position, movement)
        assert(!initial.isStageOver)
        assert(result.isInvalid)
        assert(result.getError == "Some other reason")
      }
    }

    it("next stage") {
      val settings = mock[GameSettings]
      val board = mock[OwnBoard]

      val result = PlayerStateMovement(board, Map.empty, false).nextStage(settings)
      assert(result == PlayerStateShooting(board, Map.empty, true))
    }
  }

  describe("shooting stage") {

    it("shoot a shot") {
      val initial = PlayerStateShooting(Map.empty, Map.empty, true)
      val result = initial.shoot()

      assert(!initial.isStageOver)
      assert(result.isValid)
      assert(!result.get.asInstanceOf[PlayerStateShooting].shotToShoot)
    }

    it("cannot shoot a shot you don't have") {
      val initial = PlayerStateShooting(Map.empty, Map.empty, false)
      val result = initial.shoot()

      assert(initial.isStageOver)
      assert(result.isInvalid)
      assert(result.getError == "You do not have a shot available")
    }

    it("next stage") {
      val settings = mock[GameSettings]
      val board = mock[OwnBoard]

      val result = PlayerStateShooting(board, Map.empty, true).nextStage(settings)
      assert(result == PlayerStateMovement(board, Map.empty, true))
    }
  }
}
