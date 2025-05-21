package io.github.teonistor.bsms

package object data {
  type Position = Vector[Int]
  type OwnBoard = Map[Position, Either[OceanCell.Occupied, ShipInPlay]]
  type OpponentBoard = Map[Position, ShootingEffect]
}
