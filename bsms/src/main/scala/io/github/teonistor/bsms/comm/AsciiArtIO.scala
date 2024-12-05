package io.github.teonistor.bsms.comm

import java.awt.FlowLayout
import java.awt.event.{KeyAdapter, KeyEvent}
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import javax.swing.{JFrame, JLabel}

class AsciiArtIO(aliceMove:    Vector[Int] => Unit,
                 aliceToggle:  () => Unit,
                 aliceConfirm: () => Unit,
                 bobMove:      Vector[Int] => Unit,
                 bobToggle:    () => Unit,
                 bobConfirm:   () => Unit) {

  private val window = new JFrame("Battleship Minesweeper Input Box")

  window.setLayout(new FlowLayout())
  window.add(new JLabel("<i>Keep this</i> window in focus"))
  window.setDefaultCloseOperation(DISPOSE_ON_CLOSE)
  window.setVisible(true)
  window.setSize(window.getPreferredSize)

  window.addKeyListener(new KeyAdapter {
    override def keyTyped(e: KeyEvent): Unit =
      e.getKeyChar.toLower match {
        case 'w' => aliceMove(Vector( 0,-1))
        case 'a' => aliceMove(Vector(-1, 0))
        case 's' => aliceMove(Vector( 0, 1))
        case 'd' => aliceMove(Vector( 1, 0))
        case 'z' => aliceToggle ()
        case 'x' => aliceConfirm()

        case 'i' => bobMove(Vector( 0,-1))
        case 'j' => bobMove(Vector(-1, 0))
        case 'k' => bobMove(Vector( 0, 1))
        case 'l' => bobMove(Vector( 1, 0))
        case 'm' => bobToggle ()
        case ',' => bobConfirm()
        case _=>
      }
  })
}
