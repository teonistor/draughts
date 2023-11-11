package io.github.teonistor.commongaming

import io.vavr.control.Validation.{invalid, valid}
import org.mockito.BDDMockito.`given`
import org.mockito.Mockito.verify
import org.mockito.scalatest.IdiomaticMockito
import org.scalatest.funsuite.AnyFunSuiteLike
import org.springframework.test.util.ReflectionTestUtils.setField
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.SECONDS
import scala.collection.mutable
import scala.util.Random.nextInt

class GamesHolderTest extends IdiomaticMockito with AnyFunSuiteLike {

  test("Start new game") {
    val gameFactory = mock[TestSettings => TestGame]
    val view = mock[HyperView[TestGame]]
    val onStart = mock[String => Unit]
    given(gameFactory(TestSettings())) willReturn TestGame()

    val holder = new GamesHolder(gameFactory, (-24, SECONDS), view, onStart)
    val key = holder.start(TestSettings())

    assert(holder.getGame(key).get == TestGame())
    verify(view).display(key, TestGame())
    verify(onStart)(key)
  }

  test("Progress when valid") {
    val view = mock[HyperView[TestGame]]
    val expectedInput = mock[TestGame]
    val expectedOutput = mock[TestGame]

    val holder = new GamesHolder[TestGame, TestSettings](null, (-24, SECONDS), view, null)
    setField(holder, "_games", mutable.Map("1234" -> expectedInput))

    holder.progress("1234", actualInput => {
      assert(actualInput == expectedInput)
      valid(expectedOutput)
    })

    assert(holder.getGame("1234").get == expectedOutput)
    verify(view).display("1234", expectedOutput)
  }

  test("Progress when invalid") {
    val view = mock[HyperView[TestGame]]
    val unchanged = mock[TestGame]

    val holder = new GamesHolder[TestGame, TestSettings](null, (-24, SECONDS), view, null)
    setField(holder, "_games", mutable.Map("1234" -> unchanged))

    holder.progress("1234", actualInput => {
      assert(actualInput == unchanged)
      invalid("Busted!")
    })

    assert(holder.getGame("1234").get == unchanged)
    verify(view).announce("1234", "Busted!")
  }

  test("Progress when key missing") {
    val view = mock[HyperView[TestGame]]
    val unchanged = mock[TestGame]

    val holder = new GamesHolder[TestGame, TestSettings](null, (-24, SECONDS), view, null)
    setField(holder, "_games", mutable.Map("1234" -> unchanged))

    holder.progress("5678", null)

    assert(holder.getGame("1234").get == unchanged)
    verify(view).announce("5678", "Nonexistent game 5678")
  }

  test("expire after given time") {
    val executor = Executors.newSingleThreadScheduledExecutor()
    val seconds = nextInt(3) + 3
    val view = mock[HyperView[TestGame]]
    val holder = new GamesHolder[TestGame, TestSettings](_=> TestGame(), (seconds, SECONDS), view, _=> ())

    val gid1 = holder.start(null)
    var gid2 = "nil"

    Seq[(Int, Runnable)](
      2 -> (() => {
        assert(holder.getGame(gid1).isDefined)
        gid2 = holder.start(null)
        assert(holder.getGame(gid2).isDefined)
      }),
      seconds + 1 -> (() => {
        assert(holder.getGame(gid1).isEmpty)
        assert(holder.getGame(gid2).isDefined)
     }),
      seconds + 3 -> (() => {
        assert(holder.getGame(gid1).isEmpty)
        assert(holder.getGame(gid2).isEmpty)
      }))
      .map { case (seconds, runnable) => executor.schedule(runnable, seconds, SECONDS)}
      .map(_.get())

    executor.shutdown()
    verify(view).display(gid1, TestGame())
    verify(view).display(gid2, TestGame())
  }

  private case class TestGame()
  private case class TestSettings()
}
