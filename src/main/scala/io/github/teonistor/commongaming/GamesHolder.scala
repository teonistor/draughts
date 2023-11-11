package io.github.teonistor.commongaming

import io.vavr.control.Validation
import io.vavr.control.Validation.invalid
import org.apache.commons.collections4.map.PassiveExpiringMap
import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters.MapHasAsScala

class GamesHolder[GAME,-SETTINGS](gameMaker: SETTINGS => GAME,
                                  gameIdleTime: (Int, TimeUnit),
                                  hyperView: HyperView[GAME],
                                  onStart: String => Unit) {

  private[this] val _games = new PassiveExpiringMap[String,GAME](gameIdleTime._1, gameIdleTime._2).asScala

  def getGame(gid:String): Option[GAME] = _games.get(gid)

  def start(settings: SETTINGS): String = {
    val generatedKey = System.currentTimeMillis().toString
    onStart(generatedKey)
    displayAndAssign(generatedKey, gameMaker(settings))
    generatedKey
  }

  def progress(key: String, function: GAME => Validation[String, GAME]): Unit =
    _games.get(key)
      .fold[Validation[String, GAME]](invalid("Nonexistent game " + key))(function)
      .fold(hyperView.announce(key, _), displayAndAssign(key, _))

  private def displayAndAssign(key: String, game: GAME): Unit = {
    hyperView.display(key, game)
    _games.put(key, game)
  }
}
