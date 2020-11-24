package io.github.teonistor.ttt.ws

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class TttController(ws: SimpMessagingTemplate) {

  @MessageMapping(Array("/click"))
//  @SendTo(Array("/topic/messages"))
  def receive(message: Array[Int]): Unit = {

    print(s"Received ${message(0)}, ${message(1)}")

  }

  def send(message: Any): Unit = {
    ws.convertAndSend("/ttt/board", message)
    println("TBC " + message)
  }
}
