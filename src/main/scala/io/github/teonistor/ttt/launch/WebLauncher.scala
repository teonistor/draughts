package io.github.teonistor.ttt.launch

import io.github.teonistor.ttt.ControlLoop
import io.github.teonistor.ttt.ws.{WsConfig, WsController}
import org.springframework.boot.SpringApplication.run

import scala.util.Using

object WebLauncher {
  def main(arg: Array[String]) {
    Using(run(classOf[WsConfig])) { applicationContext =>

      val controller = applicationContext.getBean(classOf[WsController])
      new ControlLoop(controller, controller).launch()
    }
  }
}
