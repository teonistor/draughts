package io.github.teonistor.connectn.termview

import io.github.teonistor.connectn.Game
import io.github.teonistor.connectn.data.{Color, GameSettings, GameState}
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream
import org.scalatest.funsuite.AnyFunSuiteLike

import java.io.OutputStream

class TermViewTest extends AnyFunSuiteLike {

  test("Cannot do higher dimensions (yet)") {
    assert(intercept[UnsupportedOperationException](TermView.display(new Game(
          GameSettings(Vector(7, 8), 6, 4),
          GameState(Map.empty, Color.Red)),
        identity))
      .getMessage == "Currently only supporting 1-dimensional base")
  }

  test("Basic") {
    val result = TermView.display(new Game(
      GameSettings(Vector(7), 6, 4),
      GameState(Map(
          Vector(0) -> List(Color.Red, Color.Red),
          Vector(3) -> List(Color.Yellow, Color.Yellow, Color.Red),
          Vector(5) -> List()),
        Color.Red)),
      identity)

    println(result)
    assert(result ==
      """|              |
         |              |
         |              |
         |       Y      |
         | R     Y      |
         | R     R      |
         | 0 1 2 3 4 5 6
         | Red to move""".stripMargin.replace("|", ""))
  }
}
