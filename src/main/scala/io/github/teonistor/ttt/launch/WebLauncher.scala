package io.github.teonistor.ttt.launch

import io.github.teonistor.ttt.ControlLoop
import io.github.teonistor.ttt.ws.{TttConfig, TttController}
import org.springframework.boot.SpringApplication.run

import scala.util.Using

object WebLauncher {
  def main(arg: Array[String]) {
    Using(run(classOf[TttConfig])) { applicationContext =>

      val controller = applicationContext.getBean(classOf[TttController])
      new ControlLoop(controller, controller).launch()
    }
  }
}
