package io.github.teonistor

package object commongaming {

  type GameStartedCallback[GAME] = (String, GameConfiguration, GAME) => Unit
}
