package io.github.teonistor.ttt.ws

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class TttController(ws: SimpMessagingTemplate) {

  @MessageMapping(Array("/chat"))
//  @SendTo(Array("/topic/messages"))
  def receive(message: Array[Int]): Unit = {


  }

  def send(message: Any): Unit = {
    ws.convertAndSend("/topic/messages", message)
    println("TBC " + message)
  }
}
