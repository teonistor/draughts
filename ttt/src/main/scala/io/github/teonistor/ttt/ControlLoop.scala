package io.github.teonistor.ttt

import java.util.concurrent.Executors.newSingleThreadScheduledExecutor
import java.util.concurrent.TimeUnit.MILLISECONDS
import io.github.teonistor.ttt.ui.{Input, View}

import scala.util.Try

class ControlLoop(input: Input, view: View) {

  private val game = new Game(input, view)
  private val executor = newSingleThreadScheduledExecutor()

  def launch() {
    val future = executor.scheduleWithFixedDelay(game.launch(), 1, 1, MILLISECONDS)

    // Wait for the game to blow and then stop the executor. Ugly, but better than nothing
    Try(future.get()).getOrElse(null)
    executor.shutdown()
  }

  def shutdown() {
    executor.shutdown()
  }
}
