package io.github.teonistor.ttt.launch

import io.github.teonistor.ttt.ControlLoop
import io.github.teonistor.ttt.ws.{WsConfig, WsController}
import org.springframework.boot.SpringApplication
import org.springframework.boot.web.context.WebServerInitializedEvent

import scala.util.Using

object WebLauncher {
  def main(arg: Array[String]) {

    val app = new SpringApplication(classOf[WsConfig])
    app.addListeners((evt: WebServerInitializedEvent) => printPortBanner(evt.getWebServer.getPort))

    Using(app.run(arg:_*)) { applicationContext =>

      val controller = applicationContext.getBean(classOf[WsController])
      new ControlLoop(controller, controller).launch()
    }
  }

  private def printPortBanner(port: Int) {
    val msg = s"* Server running on http://localhost:$port/ *"
    val banner = "*" * msg.length
    println(s" $banner\n $msg\n $banner")
  }
}
