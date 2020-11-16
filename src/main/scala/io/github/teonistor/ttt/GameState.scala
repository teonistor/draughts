package io.github.teonistor.ttt

class GameState private (val xs: Set[(Int,Int)], val os: Set[(Int,Int)]){

  def free(i: Int, j: Int) = !(xs.contains((i, j)) || os.contains((i, j)))

  def x(i: Int, j: Int) = new GameState(xs.incl((i, j)), os)

  def o(i: Int, j: Int) = new GameState(xs, os.incl((i, j)))
}

object GameState {
  def apply() = new GameState(Set.empty, Set.empty)
}
