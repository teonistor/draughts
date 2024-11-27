package io.github.teonistor.connectn

import io.github.teonistor.connectn.data.{GameOverChecker, GameSettings, GameState}
import io.vavr.control.Validation
import io.vavr.control.Validation.{invalid => Invalid, valid => Valid}

class Game(val settings: GameSettings,val history: List[GameState]) {
  if (history.isEmpty)
    throw new IllegalArgumentException("Cannot create game without a starting state")

  def this(settings: GameSettings, state: GameState) =
    this(settings, List(state))

  lazy val (isGameOver, winner) = GameOverChecker.isGameOver(this)

  def placePieceAt(column: Vector[Int]): Validation[String, Game] = {

    if (column.size!=settings.baseDimensions.size)
      return Invalid(s"Invalid ${column.size}-dimensional input in game with ${settings.baseDimensions.size}-dimensional base")

    val tooLow = column.find(_<0)
    if (tooLow.isDefined)
      return Invalid(s"Invalid input ${tooLow.get}<0")

    val tooHigh = (column zip settings.baseDimensions).find { case(dimIn, dimBase) =>
          dimIn <0 ||dimIn >=dimBase}
    if (tooHigh.isDefined)
      return Invalid(s"Invalid input ${tooHigh.get._1}>=${tooHigh.get._2}")

    val columnUpd = history.head.board
      .getOrElse(column, List.empty)
      .prepended(history.head.currentPlayer)
    if (columnUpd.size > settings.height)
      return Invalid(s"Invalid input: column ${niceColumn(column)} is full")

    if (isGameOver)
      return Invalid("Game over")

    Valid(new Game(
      settings,
      history.prepended(GameState(
        history.head.board.updated(column, columnUpd),
        history.head.currentPlayer.next))))
  }

  def currentState: GameState =
    history.head

  private def niceColumn(column: Vector[Int]) =
    if (column.size > 1)
      column.mkString("(", ",", ")")
    else
      column.head
}
