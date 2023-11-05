package io.github.teonistor.draughts.srlz

import io.github.teonistor.draughts.Player

object PlayerModule extends EnumeratedTypeModule("PlayerModule", Map(
  Player.black -> "B",
  Player.white -> "W")) {}
