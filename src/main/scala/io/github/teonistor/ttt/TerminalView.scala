package io.github.teonistor.ttt

import io.vavr.collection.Stream.rangeClosed

class TerminalView {

  def view(state: GameState): Unit = {
    val (left, right, top, bottom) = state.margins

//    println(s"left=$left right=$right top=$top bottom=$bottom")

    val indication = s"Top-left corner: line $top, column $left"
  // TODO Top and bottom grid line
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
    print("Place " + state.player)
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
