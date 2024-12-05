package io.github.teonistor.bsms.comm

import org.mockito.ArgumentMatchers.{any, refEq}
import org.mockito.IdiomaticMockito
import org.mockito.Mockito.mockConstruction
import org.mockito.captor.{ArgCaptor => captor}
import org.scalatest.funspec.AnyFunSpec

import java.awt.event.{KeyEvent, KeyListener}
import java.awt.{Component, FlowLayout}
import javax.swing.JFrame
import javax.swing.WindowConstants.DISPOSE_ON_CLOSE
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Using

class AsciiArtIoTest extends AnyFunSpec with IdiomaticMockito {

  describe("Window") {

    val aliceMove = mock[Vector[Int] => Unit]
    val aliceToggle = mock[() => Unit]
    val aliceConfirm = mock[() => Unit]
    val bobMove = mock[Vector[Int] => Unit]
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
      jFrame.setLayout(refEq(new FlowLayout())) wasCalled once
//      TODO Not sure why this doesn't take it
//      jFrame.add(refEq(new JLabel("<i>Keep this</i> window in focus"))) wasCalled once
      jFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE) wasCalled once
      jFrame.setVisible(true) wasCalled once
      jFrame.setSize(any) wasCalled once
    }

    describe("key listener") {
      val component = mock[Component]

      it("Alice moves up") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'w'))
        aliceMove(Vector(0, -1)) wasCalled once
      }

      it("Alice moves left") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'a'))
        aliceMove(Vector(-1, 0)) wasCalled once
      }

      it("Alice moves down") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 's'))
        aliceMove(Vector(0, 1)) wasCalled once
      }

      it("Alice moves right") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'd'))
        aliceMove(Vector(1, 0)) wasCalled once
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
        bobMove(Vector(0, -1)) wasCalled once
      }

      it("Bob moves left") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'j'))
        bobMove(Vector(-1, 0)) wasCalled once
      }

      it("Bob moves down") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'k'))
        bobMove(Vector(0, 1)) wasCalled once
      }

      it("Bob moves right") {
        keyListener.keyTyped(new KeyEvent(component, 0, 0, 0, 0, 'l'))
        bobMove(Vector(1, 0)) wasCalled once
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
