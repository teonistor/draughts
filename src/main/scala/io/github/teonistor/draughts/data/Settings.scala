package io.github.teonistor.draughts.data

case class Settings(startingRows: Int,
                    boardSizes: Int*) {
  if (boardSizes.size < 2)
    throw new IllegalArgumentException(s"It is not possible to play ${boardSizes.size}-dimensional Draughts")

  boardSizes.filter(_ < 2)
    .map(_.toString)
    .reduceOption(_+ ", " +_)
    .foreach(invalid => throw new IllegalArgumentException(s"All dimensions must be greater than 1 but found $invalid"))

  if (startingRows < 1)
    throw new IllegalArgumentException("It would be very boring to play Draughts with no pieces")

  if (startingRows * 2 >= boardSizes.last)
    throw new IllegalArgumentException(s"startingRows ($startingRows) must be less than half of last dimension (${boardSizes.last})")

  @Deprecated // For backwards-comp. compil
  def boardWidth = boardSizes.head
  @Deprecated // For backwards-comp. compil
  def boardHeight = boardSizes.last

/* Clarifications for multidimensional draughts
 * - Pieces can move in all directions in all but one dimension (the "original forward") - which will be the last one in the vector
 * - This special dimension is the only one where startingRows applies
 *
 * Future ideas:
 * - Customise how many dimensions are forward-restricted (and therefore startingRows applying to them... possibly with different values???)
 */

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

object Settings {

  @Deprecated // For backwards-comp. compil
  def apply(boardWidth: Int,
            boardHeight: Int,
            startingRows: Int): Settings = Settings(startingRows, Seq(boardWidth, boardHeight): _*)
}
