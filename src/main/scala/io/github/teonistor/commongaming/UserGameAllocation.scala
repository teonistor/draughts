package io.github.teonistor.commongaming

case class UserGameAllocation[GAME](
           key: Long,
           // Don't save settings here; we can expose them from the game via interface when we get to it
           game: GAME,
           allocated: Map[String, String],
           unallocated: Set[String]) {

  def isComplete = unallocated.isEmpty

}
