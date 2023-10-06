package io.github.teonistor.draughts.spring

import io.github.teonistor.commongaming.HyperView
import io.github.teonistor.draughts.data.Settings
import io.github.teonistor.draughts.{Game, GamesHolderFactory, HDUtils, Piece, Player}
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable

@Controller
class DraughtsCtrl(ws: SimpMessagingTemplate, gamesHolderFactory: GamesHolderFactory) extends HyperView[Game] {

  private lazy val gamesHolder = gamesHolderFactory(this)

  // Intermediate UI cache, so that a client joining midway sees the state right away
  // TODO PROBLEM - now we need to cache per gid AND LOSE IT WHEN IT ENDS
  private var lastDimensionCount: Int =_
  private var lastState: SendableState =_
  private var lastSettings: SendableSettings =_

  override def announce(key: String, message: String): Unit =
    ws.convertAndSend(s"/draughts/$key/message", message)

  override def announce(key: String, player: String, message: String): Unit =
    ws.convertAndSend(s"/draughts/$key/$player/message", message)

  override def display(key: String, game: Game): Unit = {
    val thing = game.availableMoves
      .flatMap(kv => {
        val (one, two, three) = layStrings1(kv._1)
        kv._2.filter(_._2.isValid).keys.map(t => {
          val (four, five, six) = layStrings1(t)
          (one, two, three, four, five, six)
        })
      })
      .groupBy(_._1).view
      .mapValues(_.groupBy(_._2).view
        .mapValues(_.groupBy(_._3).view
          .mapValues(_.groupBy(_._4).view
            .mapValues(_.groupMap(_._5)(iiiiik => (iiiiik._6, true)).view
              .mapValues(_.toMap).toMap).toMap).toMap).toMap).toMap

    lastState = SendableState(
      game.gameState.board
        .map((layStrings _).tupled)
        .groupBy(_._1).view
        .mapValues(_.groupMap(_._2)(iikv => (iikv._3, iikv._4)).view
          .mapValues(_.toMap).toMap).toMap,
      game.gameState.currentPlayer,
      thing,
      if (game.isGameOver)
        "Game over!"
      else game.gameState.ongoingJump
        .map(_.mkString("continue jumping from (", ", ", ") (or pass)"))
        .orElse(Some("move"))
        .map(game.gameState.currentPlayer + " to " +_+ ".")
        .get)
    ws.convertAndSend(s"/draughts/$key/state", lastState)
  }

  @MessageMapping(Array("/draughts/{gid}/click"))
  def receive(@PathVariable gid: String, message: (Vector[Int],Vector[Int])): Unit =
    gamesHolder.progress(gid, game => game.move(truncateExcessDimensions(message._1), truncateExcessDimensions(message._2)))

  @MessageMapping(Array("/draughts/{gid}/pass"))
  def receive(@PathVariable gid: String): Unit =
    gamesHolder.progress(gid, _.pass())

  @MessageMapping(Array("/draughts/new-game"))
  def receive(settings: Settings): Unit = {
    gamesHolder.start(settings)
    lastDimensionCount = settings.boardSizes.size
    lastSettings = SendableSettings(
      settings.startingRows,
      HDUtils.cartesianProduct(settings.boardSizes.take(settings.boardSizes.size - 5).to(Vector).map(0 until _)).map(_.mkString(",")),
      settings.boardSizes.lift(settings.boardSizes.size - 5).getOrElse(1),
      settings.boardSizes.lift(settings.boardSizes.size - 4).getOrElse(1),
      settings.boardSizes.lift(settings.boardSizes.size - 3).getOrElse(1),
      settings.boardSizes(settings.boardSizes.size - 2),  // Last 2 are guaranteed to exist thanks to Settings preconditions
      settings.boardSizes.last)

    // TODO Here
    ws.convertAndSend("/draughts/settings", lastSettings)
  }

  @SubscribeMapping(Array("/draughts/{gid}/state"))
  def onSubscribeState = lastState

  @SubscribeMapping(Array("/draughts/{gid}/settings"))
  def onSubscribeSettings = lastSettings


  private def threeWaySplit(coord: Vector[Int], default: Int) = {
    val (first, middleLast) = coord.splitAt(coord.size - 5)
    val (middle, last) = middleLast.prependedAll(List.fill(5-middleLast.size)(default)).splitAt(2)
    (first, middle, last)
  }

  private def layStrings1(k: Vector[Int]) = {
    val (first, middle, last) = threeWaySplit(k, 0)
    (first.mkString(","), middle.mkString(","), last.mkString(","))
  }

  private def layStrings(k: Vector[Int], v: Piece) = {
    val (first, middle, last) = threeWaySplit(k, 0)
    (first.mkString(","), middle.mkString(","), last.mkString(","), v)
  }

  private def truncateExcessDimensions(coord: Vector[Int]) =
    if (coord.size > lastDimensionCount) coord.drop(coord.size - lastDimensionCount) else coord

  case class SendableSettings(startingRows : Int,
                              higherIndices: Seq[String],
                              metaWidth    : Int,
                              metaHeight   : Int,
                              boardDepth   : Int,
                              boardWidth   : Int,
                              boardHeight  : Int)

  case class SendableState(board         : Map[String, Map[String, Map[String, Piece]]],
                           currentPlayer : Player,
                           availableMoves: Map[String, Map[String, Map[String, Map[String, Map[String, Map[String, Boolean]]]]]],
                           situation     : String)
}
