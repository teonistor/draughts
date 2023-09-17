package io.github.teonistor.commongaming

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.web.bind.annotation.RestController

import java.util.{List => JuList}
import scala.jdk.CollectionConverters.IterableHasAsScala

@RestController
class InsecureLobby(ws: SimpMessagingTemplate, gameConfigurations: JuList[GameConfiguration]) {

  private val configs = gameConfigurations.asScala.groupMapReduce(_.name)(identity)((_,_) => throw new IllegalArgumentException)
  private var allocations: Map[Long, UserGameAllocation] = Map.empty

  @MessageMapping(Array("/lobby/create"))
  def create(message: String): Unit = {
    val now = System.currentTimeMillis()
    assignAndSend(allocations.updated(now, UserGameAllocation(now, Map("m" -> "37", "o" -> "99"), Set("n"))))
  }

  @MessageMapping(Array("/lobby/allocate"))
  def allocate(message: (Long, String, String)): Unit = message match {
    case (game, player, user) => allocations
      .get(game).foreach(allocation => Option(player)
        .filter(allocation.unallocated)
        .map(player => Option(user)
          .filter(isUserAllowed(game))
          .map(user => allocations.updated(allocation.key, allocation.copy(
            allocated = allocation.allocated.updated(player, user),
            unallocated = allocation.unallocated.excl(player))))
          .foreach(assignAndSend)))
  }

  @MessageMapping(Array("/lobby/deallocate"))
  def deallocate(message: (Long, String, String)): Unit = message match {
    case (game, player, user) => allocations
      .get(game).foreach(allocation => Option(player)
        .filter(allocation.allocated.get(_).contains(user))
        .map(player => allocations.updated(allocation.key, allocation.copy(
          allocated = allocation.allocated.removed(player),
          unallocated = allocation.unallocated.incl(player))))
        .foreach(assignAndSend))
  }

  @SubscribeMapping(Array("/lobby-state"))
  def onSubscribeState = allocations

  private def isUserAllowed(game: Long): String => Boolean =
    user => allocations.valuesIterator.forall(alc => alc.key == game || !alc.allocated.valuesIterator.contains(user))

  private def assignAndSend(allocations: Map[Long, UserGameAllocation]): Unit = {
    this.allocations = allocations
    println(allocations)
    ws.convertAndSend("/lobby/lobby-state", allocations)
  }
}
