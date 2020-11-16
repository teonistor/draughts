package io.github.teonistor.ttt

import io.vavr.collection.Stream.rangeClosed

class TerminalView {

  def view(state: GameState): Unit = {
    val (is,js) = LazyList.concat(state.xs, state.os).unzip

    val (left, right, top, bottom) =
      if (is.isEmpty) (-2, 2, -2, 2)
      else (js.min - 2, js.max + 2, is.min - 2, is.max + 2)

//    println(s"left=$left right=$right top=$top bottom=$bottom")

    val indication = s"Top-left corner: line $top, column $left"
  // TODO Top and bottom line
    val board = rangeClosed(top, bottom).map(i =>
      rangeClosed(left, right).map(j =>
        if (state.xs.contains((i, j))) " X "
        else if (state.os.contains((i, j))) " O "
        else "   ")
        .intersperse("│").prepend("│").append("│\n")
        .reduce(_ + _))
      .intersperse(rangeClosed(left, right).map(_ => "───")
        .intersperse("┼").prepend("├").append("┤\n")
        .reduce(_ + _))
      .reduce(_ + _)

    println(indication)
    println(board)
//      1,1,1,1)

//    if (is.isEmpty) {
//      left = bottom = -2
//      right = top = 2
//    }
//    LazyList.from(is).max

//    io.vavr.collection.Stream.concat(state.xs, state.os)
//      .
  }
}

object TerminalView {
  def main(args: Array[String]): Unit = {
    new TerminalView().view(GameState().x(-1,0).o(0,2))
  }
}
