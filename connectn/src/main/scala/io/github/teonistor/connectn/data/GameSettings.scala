package io.github.teonistor.connectn.data

case class GameSettings(baseDimensions: Vector[Int],
                        height: Int,
                        winningThreshold: Int) {
  // TODO Validation
}
