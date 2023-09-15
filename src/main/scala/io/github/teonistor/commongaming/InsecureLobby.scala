package io.github.teonistor.commongaming

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class InsecureLobby(ws: SimpMessagingTemplate) {

  private var allocations: Map[Long, UserGameAllocation[_]] = Map.empty

  @MessageMapping(Array("/lobby/create"))
  def create(message: String): Unit = {
    val now = System.currentTimeMillis()
    assignAndSend(allocations.updated(now, UserGameAllocation(now, null, Map.empty, Set("m", "n"))))
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

  private def assignAndSend(allocations: Map[Long, UserGameAllocation[_]]): Unit = {
    this.allocations = allocations
    ws.convertAndSend("/lobby/lobby-state", allocations)
  }
}
