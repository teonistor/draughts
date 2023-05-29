package io.github.teonistor.draughts.data

case class Settings(boardWidth: Int,
                    boardHeight: Int,
                    startingRows: Int) {
  if (startingRows * 2 >= boardHeight) throw new IllegalArgumentException(s"startingRows ($startingRows) must be less than half of boardHeight ($boardHeight)")
}
