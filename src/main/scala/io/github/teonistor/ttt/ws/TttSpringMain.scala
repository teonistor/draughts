package io.github.teonistor.ttt.ws

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class TttSpringMain {}

object TttSpringMain {
  def main(arg: Array[String]): Unit = {
    SpringApplication.run(classOf[TttSpringMain])
  }
}
