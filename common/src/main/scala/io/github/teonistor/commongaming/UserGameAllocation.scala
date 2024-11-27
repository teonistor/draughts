package io.github.teonistor.commongaming

case class UserGameAllocation(
     key: String,
     name: String,
     allocated: Map[String, String],
     unallocated: Set[String]) {
}
