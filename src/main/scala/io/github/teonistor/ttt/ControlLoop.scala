package io.github.teonistor.ttt

import java.util.concurrent.Executors.newSingleThreadScheduledExecutor
import java.util.concurrent.TimeUnit.MILLISECONDS

object ControlLoop {

  def main(args: Array[String]): Unit = {

    val executor = newSingleThreadScheduledExecutor()

    executor.scheduleWithFixedDelay(new Game(), 1, 1, MILLISECONDS);

  }
}
