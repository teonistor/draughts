package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.data.Movement
import io.github.teonistor.bsms.data.Movement.{down, left, right, up}

import java.awt.FlowLayout
import java.awt.event.{KeyAdapter, KeyEvent}
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import javax.swing.{JFrame, JLabel}

class AsciiArtIO(aliceMove:    Movement => Unit,
                 aliceToggle:  () => Unit,
                 aliceConfirm: () => Unit,
                 bobMove:      Movement => Unit,
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
        case 'w' => aliceMove(up)
        case 'a' => aliceMove(left)
        case 's' => aliceMove(down)
        case 'd' => aliceMove(right)
        case 'z' => aliceToggle ()
        case 'x' => aliceConfirm()

        case 'i' => bobMove(up)
        case 'j' => bobMove(left)
        case 'k' => bobMove(down)
        case 'l' => bobMove(right)
        case 'm' => bobToggle ()
        case ',' => bobConfirm()
        case _=>
      }
  })
}
