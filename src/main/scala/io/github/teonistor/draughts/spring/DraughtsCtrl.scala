package io.github.teonistor.draughts.spring

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
class DraughtsCtrl(ws: SimpMessagingTemplate, junctureFactory: View=>Juncture) extends View {

  private lazy val juncture = junctureFactory(this)

  // Intermediate UI cache, so that a client joining midway sees the state right away
  private var lastDimensionCount: Int =_
  private var lastState: SendableState =_
  private var lastSettings: SendableSettings =_

  override def announce(message: String): Unit =
    ws.convertAndSend("/draughts/draughts-message", message)

  override def display(game: Game): Unit = {
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
    ws.convertAndSend("/draughts/draughts-state", lastState)
  }

  @MessageMapping(Array("/click"))
  def receive(message: (Vector[Int],Vector[Int])): Unit =
      juncture.progress(game => game.move(truncateExcessDimensions(message._1), truncateExcessDimensions(message._2)))

  @MessageMapping(Array("/pass"))
  def receive(): Unit =
    juncture.progress(_.pass())

  @MessageMapping(Array("/new-game"))
  def receive(settings: Settings): Unit = {
    juncture.start(settings)
    lastDimensionCount = settings.boardSizes.size
    lastSettings = SendableSettings(
      settings.startingRows,
      HDUtils.cartesianProduct(settings.boardSizes.take(settings.boardSizes.size - 5).to(Vector).map(0 until _)).map(_.mkString(",")),
      settings.boardSizes.lift(settings.boardSizes.size - 5).getOrElse(1),
      settings.boardSizes.lift(settings.boardSizes.size - 4).getOrElse(1),
      settings.boardSizes.lift(settings.boardSizes.size - 3).getOrElse(1),
      settings.boardSizes(settings.boardSizes.size - 2),  // Last 2 are guaranteed to exist thanks to Settings preconditions
      settings.boardSizes.last)
    ws.convertAndSend("/draughts/draughts-settings", lastSettings)
  }

  @SubscribeMapping(Array("/draughts-state"))
  def onSubscribeState = lastState

  @SubscribeMapping(Array("/draughts-settings"))
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
