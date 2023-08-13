package io.github.teonistor.draughts.spring

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.teonistor.draughts.data.Settings
import io.github.teonistor.draughts.{Game, HDUtils, Juncture, Piece, Player, View}
import org.springframework.context.annotation.Lazy
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.web.bind.annotation.{RequestMapping, RestController}

@Lazy
@RestController
@RequestMapping(Array("draughts-api"))
class Ctrl(om: ObjectMapper, ws: SimpMessagingTemplate, junctureFactory: View=>Juncture) extends View {

  private lazy val juncture = junctureFactory(this)

  // Intermediate UI cache, so that a client joining midway sees the state right away
  private var lastState: SendableState =_
  private var lastSettings: SendableSettings =_

  override def announce(message: String): Unit =
    ws.convertAndSend("/draughts/draughts-message", message)

  override def display(game: Game): Unit = {
    lastState = SendableState(
      game.gameState.board.to(LazyList).map { case (position,piece) => val (a,b) = position.splitAt(position.size - 3); (a, b, piece) },
      game.gameState.currentPlayer,
      game.gameState.ongoingJump,
      if (game.isGameOver)
        "Game over!\n"
      else game.gameState.ongoingJump
        .map(_.mkString("continue jumping from (", ", ", ") (or pass)"))
        .orElse(Some("move"))
        .map(game.gameState.currentPlayer + " to " +_+ ".\n")
        .get)
    ws.convertAndSend("/draughts/draughts-state", lastState)
  }

  @MessageMapping(Array("/click"))
  def receive(message: (Vector[Int],Vector[Int])): Unit =
      juncture.progress(game => game.move _ tupled message)

  @MessageMapping(Array("/pass"))
  def receive(): Unit =
    juncture.progress(_.pass())

  @MessageMapping(Array("/new-game"))
  def receive(settings: Settings): Unit = {
    juncture.start(settings)
    lastSettings = SendableSettings(
      settings.boardSizes,
      HDUtils.cartesianProduct(settings.boardSizes.take(settings.boardSizes.size - 3).to(Vector).map(0 until _)),
      SendableLast3D(
        settings.boardSizes.lift(settings.boardSizes.size - 3).getOrElse(1),
        settings.boardSizes(settings.boardSizes.size - 2),  // Last 2 are guaranteed to exist due to Settings preconditions
        settings.boardSizes.last))
    ws.convertAndSend("/draughts/draughts-settings", lastSettings)
  }

  @SubscribeMapping(Array("/draughts-state"))
  def onSubscribeState = lastState

  @SubscribeMapping(Array("/draughts-settings"))
  def onSubscribeSettings = lastSettings


  case class SendableSettings(boardSizes:Seq[Int],
                              partialIndices:Seq[Vector[Int]],
                              last3D: SendableLast3D)

  case class SendableLast3D(depth: Int, width: Int, height: Int)

  case class SendableState(board: LazyList[(Vector[Int], Vector[Int], Piece)],
                           currentPlayer: Player,
                           ongoingJump: Option[Vector[Int]],
                           situation: String)
}
