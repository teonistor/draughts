package io.github.teonistor.ttt.ws

import java.util.concurrent.ArrayBlockingQueue

import io.github.teonistor.ttt.GameState
import io.github.teonistor.ttt.ui._
import io.vavr.collection.Stream.rangeClosed
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller

@Controller
class WsController(ws: SimpMessagingTemplate) extends Input with View {

  private val q = new ArrayBlockingQueue[(Int,Int)](1)
  private var lastSentState: Array[Any] = Array(0, 0, Array(""), "")

  @MessageMapping(Array("/click"))
  def receive(message: Array[Int]) {
    q.put((message(0), message(1)))
  }

  @SubscribeMapping(Array("/board"))
  def sendOnSubscribe(): Array[Any] = lastSentState

  override def takeInput() = q.take()

  override def view(state: GameState) {
    val (left, right, top, bottom) = state.margins

    val board = rangeClosed(top, bottom).map(i => rangeClosed(left, right).map(j =>

        if (state.xs.contains((i, j))) "X"
        else if (state.os.contains((i, j))) "O"
        else " "

      ).reduce((_: String) + (_: String)) // Why types?
    ).toJavaList()

    lastSentState = Array(top, left, board, state.player.toString)
    ws.convertAndSend("/ttt/board", lastSentState)
  }

  override def announceWinner(winner: String) {
    ws.convertAndSend("/ttt/winner", winner)
  }
}
