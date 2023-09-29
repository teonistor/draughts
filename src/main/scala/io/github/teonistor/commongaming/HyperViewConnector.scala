package io.github.teonistor.commongaming

object HyperViewConnector {

  def connect[GAME](key: String, hyperView: HyperView[GAME]) = new View[GAME]() {

    override def announce(message: String): Unit = hyperView.announce(key, message)

    override def announce(player: String, message: String): Unit = hyperView.announce(key, player, message)

    override def display(game: GAME): Unit = hyperView.display(key, game)
  }
}
