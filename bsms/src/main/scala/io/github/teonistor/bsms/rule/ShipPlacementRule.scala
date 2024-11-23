package io.github.teonistor.bsms.rule

import io.github.teonistor.bsms.core.Orientation
import io.github.teonistor.bsms.data.OceanCell.{damagedShip, healthyShip, mine}
import io.github.teonistor.bsms.data.{OwnBoard, Position, ShipDescription, ShipInPlay}
import io.vavr.control.Validation
import io.vavr.control.Validation.{invalid, valid}

object ShipPlacementRule {

  def placeShip(board: OwnBoard, ship: ShipDescription, position: Position, orientation: Orientation): Validation[String, OwnBoard] = {
    val positions = (0 until ship.length)
      .map(d => position.zipWithIndex.map(p => (p, orientation) match {
        case ((coord, 0), Orientation.horizontal) => coord + d
        case ((coord, 1), Orientation.vertical) => coord + d
        case ((coord, _), _) => coord
      }))

    placeShip0(board, ship.name, positions)
  }

  def moveShip(board: OwnBoard, position: Position, movement: Vector[Int]): Validation[String, OwnBoard] = {
    val vp = movement.groupMapReduce(identity)(_=>1)(_+_)
    // This is quite horrible
    if ((vp.keySet -- Set(-1,0,1)).nonEmpty || (vp.keySet - 0).size != 1 || vp.removed(0).values.toSet != Set(1))
      return invalid("Ship must move exactly one space in the direction it is oriented")

    val hopefullyShip = board.get(position).flatMap(_.toOption)
    if (hopefullyShip.isEmpty)
      return invalid(position.mkString("You don't have a ship at (", ",", ")"))

    val Some(ShipInPlay(name, parts)) = hopefullyShip

    // This is also quite horrible and together with the above horribility shows an abstraction is missing
    val mi = movement.zipWithIndex.find {case (v,_) => v==1 || v == -1} .get._2
    if (parts.keys.map(_(mi)).toSet.size == 1)
      return invalid("Ship must move in the direction it is oriented")

    val lifted = board.removedAll(parts.keys)
    val poss = parts.keySet.map(_.lazyZip(movement).map(_+_))

    placeShip0(lifted, name, poss)
  }

  private def placeShip0(board: OwnBoard, name: String, positions: Iterable[Vector[Int]]): Validation[String, OwnBoard] = {
    if (positions.exists(board.get(_).exists(_.isRight)))
      return invalid("Cannot place ship on top of another")

    val spawnedShip = ShipInPlay(name, positions
      .map(pos => (pos, board.get(pos).filter(_== Left(mine)).map(_=> damagedShip).getOrElse(healthyShip)))
      .toMap)

    valid(board ++ positions.map((_, Right(spawnedShip))))
  }
}
