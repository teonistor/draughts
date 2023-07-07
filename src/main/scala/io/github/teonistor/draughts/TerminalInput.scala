package io.github.teonistor.draughts

import io.github.teonistor.draughts.data.Settings

import java.io.{BufferedReader, InputStream, InputStreamReader}
import scala.annotation.tailrec
import scala.util.{Try, Using}

class TerminalInput(inputStream: InputStream, juncture: Juncture) extends Runnable {
  private val moveInput = "[\\d ]+".r

  def run(): Unit =
    Using(new BufferedReader(new InputStreamReader(inputStream)))(runLine).get

  @tailrec
  private def runLine(reader: BufferedReader): Unit = {
    val line = reader.readLine().strip()

    if ("pass" equalsIgnoreCase line)
      juncture.progress(_.pass())

    else if("new game" equalsIgnoreCase line)
      readSettings(reader).foreach(juncture.start)

    else if(moveInput matches line) {
      val u = line.split(" +").map(_.toInt).to(Vector)
      val (from, to) = u.splitAt(u.length / 2)
      if (from.size == to.size)
        juncture.progress(_.move(from, to))

    }

    if (!"exit".equalsIgnoreCase(line))
      runLine(reader)
  }

  private def readSettings(reader: BufferedReader): Option[Settings] = {
    print("Starting rows (default 2): ")
    val startingRow = reader.readLine().strip().toIntOption.getOrElse(2)
    println(startingRow)

    print("Dimensions (default 8 8): ")
    val boardSizes = Option(reader.readLine().strip())
      .filter(moveInput.matches)
      .map(_.split(" +").map(_.toInt).to(Vector))
      .getOrElse(Vector(8, 8))
    println(boardSizes)

    Try(Settings(startingRow, boardSizes: _*))
      .fold(e => {println(e); None}, Option(_))
  }
}
