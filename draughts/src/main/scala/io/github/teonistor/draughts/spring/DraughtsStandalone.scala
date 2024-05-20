package io.github.teonistor.draughts.spring

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class DraughtsStandalone {}

object DraughtsStandalone {
  def main(arg: Array[String]): Unit = {
    System.setProperty("server.port", "8090")
    SpringApplication.run(classOf[DraughtsStandalone], arg:_*)
  }
}
