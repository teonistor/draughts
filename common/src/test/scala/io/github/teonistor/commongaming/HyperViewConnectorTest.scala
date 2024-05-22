package io.github.teonistor.commongaming

import org.mockito.MockitoSugar
import org.scalatest.funsuite.AnyFunSuiteLike

class HyperViewConnectorTest extends MockitoSugar with AnyFunSuiteLike {

  test("connected announce all") {
    val hyperView = mock[HyperView[Float]]

    HyperViewConnector.connect("my key", hyperView).announce("my message")

    verify(hyperView).announce("my key", "my message")
  }

  test("connected announce specific player") {
    val hyperView = mock[HyperView[Float]]

    HyperViewConnector.connect("my other key", hyperView).announce("you", "your message")

    verify(hyperView).announce("my other key", "you", "your message")
  }

  test("connected display") {
    val hyperView = mock[HyperView[Float]]

    HyperViewConnector.connect("your key", hyperView).display(3.14f)

    verify(hyperView).display("your key", 3.14f)
  }
}
