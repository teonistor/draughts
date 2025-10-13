package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.data.Movement
import io.github.teonistor.bsms.data.Movement.{down, left, right, up}
import org.mockito.ArgumentMatchers.any
import org.mockito.IdiomaticMockito
import org.mockito.Mockito.mockConstruction
import org.mockito.captor.{ArgCaptor => captor}
import org.scalatest.funspec.AnyFunSpec

import java.awt.Component
import java.awt.event.{KeyEvent, KeyListener}
import javax.swing.JFrame
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Using

class AsciiArtIoTest extends AnyFunSpec with IdiomaticMockito {

  describe("Window") {

    val aliceMove = mock[Movement => Unit]
    val aliceToggle = mock[() => Unit]
    val aliceConfirm = mock[() => Unit]
    val bobMove = mock[Movement => Unit]
    val bobToggle = mock[() => Unit]
    val bobConfirm = mock[() => Unit]

    val (howManyConstructed, jFrame, keyListener) = Using(mockConstruction(classOf[JFrame])) {
      jFrameConstruction =>
        new AsciiArtIO(aliceMove, aliceToggle, aliceConfirm, bobMove, bobToggle, bobConfirm)
        val jFramesConstructed = jFrameConstruction.constructed().asScala

        val keyListenerCap = captor[KeyListener]
        jFramesConstructed.head.addKeyListener(keyListenerCap) wasCalled once
        val keyListener = keyListenerCap.value

        (jFramesConstructed.size, jFramesConstructed.head, keyListener)
    }.get

    it("one is constructed") {
      assert(howManyConstructed == 1)
    }

    it("parameters are set") {
//      TODO Not sure why this doesn't take it
//      jFrame.setLayout(refEq(new FlowLayout())) wasCalled once
//      jFrame.add(refEq(new JLabel("<i>Keep this</i> window in focus"))) wasCalled once
      jFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE) wasCalled once
      jFrame.setVisible(true) wasCalled once
      jFrame.setSize(any) wasCalled once
    }

    describe("key listener") {
      val component = mock[Component]

      it("Alice moves up") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'w'))
        aliceMove(up) wasCalled once
      }

      it("Alice moves left") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'a'))
        aliceMove(left) wasCalled once
      }

      it("Alice moves down") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 's'))
        aliceMove(down) wasCalled once
      }

      it("Alice moves right") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'd'))
        aliceMove(right) wasCalled once
      }

      it("Alice toggles") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'z'))
        aliceToggle() wasCalled once
      }

      it("Alice confirms") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'x'))
        aliceConfirm() wasCalled once
      }

      it("Bob moves up") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'i'))
        bobMove(up) wasCalled once
      }

      it("Bob moves left") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'j'))
        bobMove(left) wasCalled once
      }

      it("Bob moves down") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'k'))
        bobMove(down) wasCalled once
      }

      it("Bob moves right") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'l'))
        bobMove(right) wasCalled once
      }

      it("Bob toggles") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'm'))
        bobToggle() wasCalled once
      }

      it("Bob confirms") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, ','))
        bobConfirm() wasCalled once
      }
    }
  }
}
