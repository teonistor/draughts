package io.github.teonistor.bsms.comm

import io.github.teonistor.bsms.core.PlayerState
import io.github.teonistor.bsms.data.OwnBoard
import io.vavr.control.Validation

case class AsciiDisplayablePlayerState(state: PlayerState,
                                       cursor: Cursor,
                                       overlay: Option[OwnBoard] = None)

object AsciiDisplayablePlayerState {
  type Validated = Validation[String, AsciiDisplayablePlayerState]
}
