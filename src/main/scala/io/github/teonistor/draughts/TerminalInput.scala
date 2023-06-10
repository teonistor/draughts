package io.github.teonistor.draughts

import java.io.{BufferedReader, InputStream, InputStreamReader}
import scala.annotation.tailrec
import scala.util.Using

class TerminalInput(inputStream: InputStream, juncture: Juncture) extends Runnable {

  def run(): Unit =
    Using(new BufferedReader(new InputStreamReader(inputStream)))(runLine)

  @tailrec
  private def runLine(reader: BufferedReader): Unit = {
    val line = reader.readLine().strip()

    if ("pass" equalsIgnoreCase line)
      juncture.progress(_.pass())

    else if(!"exit".equalsIgnoreCase(line))
      runLine(reader)
  }
}
