package io.github.teonistor.ttt.ws

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors.newSingleThreadExecutor

import io.github.teonistor.ttt.ui._
import io.github.teonistor.ttt.{ControlLoop, GameState}
import io.vavr.collection.Stream.rangeClosed
import javax.annotation.{PostConstruct, PreDestroy}
import org.slf4j.LoggerFactory.getLogger
import org.springframework.context.annotation.Lazy
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller

@Controller
@Lazy
class WsController(ws: SimpMessagingTemplate) extends Input with View {

  private val q = new ArrayBlockingQueue[(Int,Int)](1)
  private val overarchingExecutor = newSingleThreadExecutor()
  private var controlLoop: ControlLoop = _
  private var lastSentState: Array[Any] = Array(0, 0, Array(""), "")

  @PostConstruct
  def postConstruct() {
    getLogger(getClass).info("Launching TTT overarching executor for the first time")
    launchNewLoop()
  }

  @MessageMapping(Array("/click"))
  def receive(message: Array[Int]) {
    q.offer((message(0), message(1)))
  }

  @SubscribeMapping(Array("/board"))
  def sendOnSubscribe(): Array[Any] = lastSentState

  override def takeInput() = q.take()

  override def view(state: GameState) {
    val (left, right, top, bottom) = state.margins

    val board = rangeClosed(top, bottom).map(i => rangeClosed(left, right).map(j =>

        if (state.xs.contains((i, j))) "X"
        else if (state.os.contains((i, j))) "O"
        else " "

      ).reduce((_: String) + (_: String)) // Why types?
    ).toJavaList()

    lastSentState = Array(top, left, board, state.player.toString)
    ws.convertAndSend("/ttt/board", lastSentState)
  }

  override def announceWinner(winner: String) {
    ws.convertAndSend("/ttt/winner", winner)
  }

  @MessageMapping(Array("/restart"))
  def restart(message: String) {
    controlLoop.shutdown()
    launchNewLoop()
  }

  private def launchNewLoop () {
    controlLoop = new ControlLoop(this, this)
    val runnable: Runnable = () => controlLoop.launch()
    overarchingExecutor.submit(runnable)
  }

  @PreDestroy
  def preDestroy() {
    controlLoop.shutdown()
    overarchingExecutor.shutdown()
  }

  /* It sorta works but we have a concurrency problem
   * When you say new game but a game is running:
   * - the control loop is shutdown() which causes the executor to be shutdown()
   * - but the running step of the game continues to run (it's probably blocked on the input queue)
   * - the new control loop which would start the new game is blocked waiting for the overarching executor
   * - the first click is consumed by the old game which now unblocks and allows its control loop to end
   * - the new game now has room on the overarching executor and starts
   * - inputs thereafter go to the new game
   *
   * It all comes down to the ability to stop the game when we need, which is nonexistent because the whole thing was written
   * with the game in the centre of the world. Conditioning the reset() would be easier but we can't do that either (server-side!)
   * because the game doesn't have a way to be interrogated whether it's still on or not.
   */
}
