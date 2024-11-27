package io.github.teonistor.draughts.spring

import io.github.teonistor.commongaming.InsecureLobby
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(Array(classOf[InsecureLobby]))
class DraughtsStandalone {}

object DraughtsStandalone {
  def main(arg: Array[String]): Unit = {
    System.setProperty("server.port", "8090")
    SpringApplication.run(classOf[DraughtsStandalone], arg:_*)
  }
}
