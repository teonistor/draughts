package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.Settings

import java.io.{BufferedReader, InputStream, InputStreamReader, PrintStream}
import scala.annotation.tailrec
import scala.util.{Try, Using}

class TerminalInput(juncture: Juncture, inputStream: InputStream, printStream: PrintStream) extends Runnable {
  private val digitsAndSpaces = "[\\d ]+".r

  def run(): Unit =
    Using(new BufferedReader(new InputStreamReader(inputStream)))(runLine).get

  @tailrec
  private def runLine(reader: BufferedReader): Unit = {
    val line = reader.readLine().strip()

    if ("pass" equalsIgnoreCase line)
      juncture.progress(_.pass())

    else if("new game" equalsIgnoreCase line)
      readSettings(reader) foreach juncture.start

    else if(digitsAndSpaces matches line) {
      val u = line.split(" +").map(_.toInt).to(Vector)
      val (from, to) = u.splitAt(u.length / 2)
      if (from.size == to.size)
        juncture.progress(_.move(from, to))
    }

    if (!"exit".equalsIgnoreCase(line))
      runLine(reader)
  }

  private def readSettings(reader: BufferedReader): Option[Settings] = {
    printStream.print("Starting rows (default 2): ")
    val startingRow = reader.readLine().strip().toIntOption.getOrElse(2)
    printStream.println(s"Got $startingRow")

    printStream.print("Dimensions (default 8 8): ")
    val boardSizes = Option(reader.readLine().strip())
      .filter(digitsAndSpaces.matches)
      .map(_.split(" +").map(_.toInt).to(Vector))
      .getOrElse(Vector(8, 8))
    printStream.println(s"Got ${boardSizes.toFriendlyString}")

    Try(Settings(startingRow, boardSizes: _*))
      .fold(e => {printStream.println(e); None}, Option(_))
  }
}
