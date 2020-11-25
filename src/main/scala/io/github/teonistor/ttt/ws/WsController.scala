package io.github.teonistor.ttt.ws

import java.util.concurrent.ArrayBlockingQueue

import io.github.teonistor.ttt.GameState
import io.github.teonistor.ttt.ui._
import io.vavr.collection.Stream.rangeClosed
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class WsController(ws: SimpMessagingTemplate) extends Input with View {

  private val q = new ArrayBlockingQueue[(Int,Int)](1)

//  @LocalServerPort // Translates to the undefined @Value("${local.server.port}")
//  private var port: Int =_
//
//  @PostConstruct
//  protected def postConstruct() {
//    println(s"Server runing on http://localhost:$port/")
//  }

  @MessageMapping(Array("/click"))
  def receive(message: Array[Int]): Unit = {
    q.put((message(0), message(1)))
  }

  override def takeInput() = q.take()

  override def view(state: GameState) {
    val (left, right, top, bottom) = state.margins

    val board = rangeClosed(top, bottom).map(i => rangeClosed(left, right).map(j =>

        if (state.xs.contains((i, j))) "X"
        else if (state.os.contains((i, j))) "O"
        else " "

      ).reduce((_: String) + (_: String)) // Why types?
    ).toJavaList()

    ws.convertAndSend("/ttt/board", Array(top, left, board))
  }

  override def announceWinner(winner: String): Unit = ???
}
