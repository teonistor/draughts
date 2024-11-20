package io.github.teonistor.bsms

import io.vavr.control.Validation

package object core {
  type ValidatedGame = Validation[String, BattleshipMinesweeper]
}
