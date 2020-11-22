package io.github.teonistor.ttt.ws

import org.springframework.messaging.handler.annotation.{MessageMapping, SendTo}
import org.springframework.stereotype.Controller

@Controller
class TttController {

  @MessageMapping(Array("/chat"))
  @SendTo(Array("/topic/messages"))
  def send(message: Array[String]): String = {
//    val time = new SimpleDateFormat("HH:mm").format(new Nothing)
//    new Nothing(message.getFrom, message.getText, time)
    "tbc"
  }
}
