package io.github.teonistor.ttt

import java.util.concurrent.Executors.newSingleThreadScheduledExecutor
import java.util.concurrent.TimeUnit.MILLISECONDS
import scala.util.Try

object ControlLoop {

  def main(args: Array[String]): Unit = {

    val executor = newSingleThreadScheduledExecutor()

    val game = new Game()
    val future = executor.scheduleWithFixedDelay(game.launch(), 1, 1, MILLISECONDS)

    // Wait for the game to blow and then stop the executor. Ugly, but better than nothing
    Try(future.get()).getOrElse(null)
    executor.shutdown()
  }
}
