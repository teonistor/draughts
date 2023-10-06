package io.github.teonistor.commongaming

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class InsecureLobby(ws: SimpMessagingTemplate) {

  private var allocations: Map[String, UserGameAllocation] = Map.empty

  def create(key:String, gameConfiguration:GameConfiguration): Unit = {
    assignAndSend(allocations.updated(key, UserGameAllocation(key, gameConfiguration.name, Map.empty, gameConfiguration.requiredPlayers)))
  }

  @MessageMapping(Array("/lobby/allocate"))
  def allocate(message: (String, String, String)): Unit = message match {
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
  def deallocate(message: (String, String, String)): Unit = message match {
    case (game, player, user) => allocations
      .get(game).foreach(allocation => Option(player)
        .filter(allocation.allocated.get(_).contains(user))
        .map(player => allocations.updated(allocation.key, allocation.copy(
          allocated = allocation.allocated.removed(player),
          unallocated = allocation.unallocated.incl(player))))
        .foreach(assignAndSend))
  }

  @SubscribeMapping(Array("/lobby/state"))
  def onSubscribeState = allocations

  private def isUserAllowed(game: String): String => Boolean =
    user => allocations.valuesIterator.forall(alc => alc.key == game || !alc.allocated.valuesIterator.contains(user))

  private def assignAndSend(allocations: Map[String, UserGameAllocation]): Unit = {
    this.allocations = allocations
    println(allocations)
    send()
  }

  private def send(): Unit =
    ws.convertAndSend("/lobby/state", allocations)
}
