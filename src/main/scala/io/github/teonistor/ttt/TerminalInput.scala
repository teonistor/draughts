package io.github.teonistor.ttt

import java.io.{BufferedReader, InputStreamReader}
import java.util.regex.Pattern

import scala.util.Try

class TerminalInput {

  private val gamePrompt = " > ".getBytes()
  private val global = Pattern.compile("\\s*(-?\\d+)[\\s,]*(-?\\d+)\\s*")

  private val outStream = System.out
  private val reader = new BufferedReader(new InputStreamReader(System.in))

  def takeInput(): (Int, Int) = Try(
    {
      outStream.write(gamePrompt)

      val matcher = global.matcher(reader.readLine())

      if (matcher.matches())
        (Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)))
      else null
    })
    .filter(_ != null)
    .getOrElse(takeInput())
}
