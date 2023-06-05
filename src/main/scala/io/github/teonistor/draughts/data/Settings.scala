package io.github.teonistor.draughts.data

case class Settings(boardWidth: Int,
                    boardHeight: Int,
                    startingRows: Int) {
  if (startingRows * 2 >= boardHeight) throw new IllegalArgumentException(s"startingRows ($startingRows) must be less than half of boardHeight ($boardHeight)")




/* Excerpts from official rules
*
* Jumping is always mandatory: if a player has the option to jump, they must take it, even if doing so results in disadvantage for the jumping player. For example, a mandated single jump might set up the player such that the opponent has a multi-jump in reply.
  Multiple jumps are possible, if after one jump, another piece is immediately eligible to be jumped by the moved piece—even if that jump is in a different diagonal direction. If more than one multi-jump is available, the player can choose which piece to jump with, and which sequence of jumps to make. The sequence chosen is not required to be the one that maximizes the number of jumps in the turn; however, a player must make all available jumps in the sequence chosen.
*
* More ideas
*
* In tournament English draughts, a variation called three-move restriction is preferred. The first three moves are drawn at random from a set of accepted openings.
*
* Two common rule variants, not recognised by player associations, are:
    Capturing with a king precedes capturing with a man. In this case, any available capture can be made at the player's choice.
    A man that has jumped to become a king can then in the same turn continue to capture other pieces in a multi-jump.

* */

}
