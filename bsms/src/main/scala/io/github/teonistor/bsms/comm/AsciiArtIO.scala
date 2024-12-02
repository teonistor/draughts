package io.github.teonistor.bsms.comm

import java.awt.FlowLayout
import java.awt.event.{KeyAdapter, KeyEvent, WindowAdapter, WindowEvent}
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import javax.swing.{JFrame, JLabel}

trait AAI {
  def aliceMove(move:Vector[Int]): Unit
  def aliceToggle(): Unit
  def aliceConfirm(): Unit

  def bobMove(move:Vector[Int]): Unit
  def bobToggle(): Unit
  def bobConfirm(): Unit
}

class AsciiArtIO(aai: AAI)
  extends JFrame("Battleship Minesweeper Input Box") with Runnable {

  setLayout(new FlowLayout())
  add(new JLabel("Keep this window in focus"))

 // setLayout(new GridBagLayout())
 // private val label = new JLabel("Keep this window in focus")
 // label.setFocusable(true)
 // add(label)
 // add(new JButton("?"))
 // add(new JButton("X"))

  setDefaultCloseOperation(DISPOSE_ON_CLOSE)
  setUndecorated(true)
  setVisible(true)
  setSize(getPreferredSize)

  addWindowListener(new WindowAdapter {
    override def windowClosed(e: WindowEvent): Unit =
      println("Closed " + Thread.currentThread())
  })

  addKeyListener(new KeyAdapter {
    override def keyTyped(e: KeyEvent): Unit =
      e.getKeyChar.toLower match {
        case 'w' => aai.aliceMove(Vector(0,-1))
        case 'a' => aai.aliceMove(Vector(-1,0))
        case 's' => aai.aliceMove(Vector(0, 1))
        case 'd' => aai.aliceMove(Vector( 1,0))
        case 'z' => aai.aliceToggle()
        case 'x' => aai.aliceConfirm()

        case 'i' => aai.bobMove(Vector(0,-1))
        case 'j' => aai.bobMove(Vector(-1,0))
        case 'k' => aai.bobMove(Vector(0, 1))
        case 'l' => aai.bobMove(Vector( 1,0))
        case 'm' => aai.bobToggle()
        case ',' => aai.bobConfirm()
        case _=>
      }
  })

  override def run(): Unit = ???
}
