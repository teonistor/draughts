package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.Position

import java.io.{BufferedReader, InputStream, InputStreamReader}
import scala.annotation.tailrec
import scala.util.Using

class TerminalInput(inputStream: InputStream, juncture: Juncture) extends Runnable {
  private val moveInput = "(-?\\d+) +(-?\\d+) +(-?\\d+) +(-?\\d+)".r

  def run(): Unit =
    Using(new BufferedReader(new InputStreamReader(inputStream)))(runLine)

  @tailrec
  private def runLine(reader: BufferedReader): Unit = {
    val line = reader.readLine().strip()

    if ("pass" equalsIgnoreCase line)
      juncture.progress(_.pass())

    else {
      val mtch = moveInput.pattern matcher line
      if (mtch.matches())
        juncture.progress(_.move(
          Position(mtch.group(1).toInt, mtch.group(2).toInt),
          Position(mtch.group(3).toInt, mtch.group(4).toInt)))

      else if (!"exit".equalsIgnoreCase(line))
        runLine(reader)
    }
  }
}
