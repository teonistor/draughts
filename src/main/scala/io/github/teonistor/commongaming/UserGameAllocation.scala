package io.github.teonistor.commongaming

case class UserGameAllocation(
     key: Long,
     allocated: Map[String, String],
     unallocated: Set[String]) {

  def isComplete = ??? // unallocated.isEmpty

}
