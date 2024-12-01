package io.github.teonistor.bsms.data

sealed trait GameCondition

object GameCondition {

  val continues:     GameCondition = new GameCondition {}
  val aliceWins:     GameCondition = new GameCondition {}
  val bobWins:       GameCondition = new GameCondition {}
  val everyoneLoses: GameCondition = new GameCondition {}
  val stalemate:     GameCondition = new GameCondition {}
}
