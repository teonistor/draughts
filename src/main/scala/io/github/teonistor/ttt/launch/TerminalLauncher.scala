package io.github.teonistor.ttt.launch

import io.github.teonistor.ttt.ControlLoop
import io.github.teonistor.ttt.ui.{TerminalInput, TerminalView}

object TerminalLauncher {
  def main(arg: Array[String]): Unit = {
    new ControlLoop(new TerminalInput, new TerminalView).launch()
  }
}
