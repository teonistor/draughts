package io.github.teonistor.commongaming

import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.RestController

@RestController
class InsecureLobby(ws: SimpMessagingTemplate) {

  private var allocations: Map[Long, UserGameAllocation[_]] = Map.empty

  @MessageMapping(Array("/lobby/allocate"))
  def allocate(message: (Long, String, String)): Unit = message match {
    case (game, player, user) => allocations
      .get(game).foreach(allocation => Option(player)
        .filter(allocation.unallocated)
        .map(player => Option(user)
          .filter(userIsAllowed(game))
          .map(user => allocations.updated(allocation.key, allocation.copy(
            allocated = allocation.allocated.updated(player, user),
            unallocated = allocation.unallocated.excl(player))))
          .foreach(allocations = _)))
  }

  @MessageMapping(Array("/lobby/deallocate"))
  def deallocate(message: (Long, String, String)): Unit = message match {
    case (game, player, user) => allocations
      .get(game).foreach(allocation => Option(player)
        .filter(allocation.allocated.get(_).contains(user))
        .map(player => allocations.updated(allocation.key, allocation.copy(
          allocated = allocation.allocated.removed(player),
          unallocated = allocation.unallocated.incl(player))))
        .foreach(allocations = _))
  }

  private def userIsAllowed(game: Long): String => Boolean =
    user => allocations.valuesIterator.forall(alc => alc.key == game || !alc.allocated.valuesIterator.contains(user))

}
