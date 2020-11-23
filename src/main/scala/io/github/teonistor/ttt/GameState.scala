package io.github.teonistor.ttt

class GameState (val xs: Set[(Int,Int)], val os: Set[(Int,Int)], val player: Player) {
//  private val xos = HashMap.from(List((Player.X, xs), (Player.O, os)))

  lazy val margins = {
    val (is, js) = LazyList.concat(xs, os).unzip

    if (is.isEmpty) (-2, 2, -2, 2)
    else (js.min - 2, js.max + 2, is.min - 2, is.max + 2)
  }

  def free(i: Int, j: Int) = !(xs.contains((i, j)) || os.contains((i, j)))

  def x(i: Int, j: Int, player: Player) = new GameState(xs.incl((i, j)), os, player)

  def o(i: Int, j: Int, player: Player) = new GameState(xs, os.incl((i, j)), player)

  def advance(i: Int, j: Int) = player match {
    case Player.X => x(i, j, player.next)
    case Player.O => o(i, j, player.next)
  }
}

object GameState extends App{
  def apply() = new GameState(Set.empty, Set.empty, Player.X)
}
