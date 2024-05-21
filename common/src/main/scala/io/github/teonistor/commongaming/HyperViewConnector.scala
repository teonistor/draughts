package io.github.teonistor.commongaming

// If we squelch Juncture, we won't need this either.
// Which is in fact good b/c not having Juncture means we move more into the base lib :)
object HyperViewConnector {

  def connect[GAME](key: String, hyperView: HyperView[GAME]) = new View[GAME]() {

    override def announce(message: String): Unit = hyperView.announce(key, message)

    override def announce(player: String, message: String): Unit = hyperView.announce(key, player, message)

    override def display(game: GAME): Unit = hyperView.display(key, game)
  }
}
