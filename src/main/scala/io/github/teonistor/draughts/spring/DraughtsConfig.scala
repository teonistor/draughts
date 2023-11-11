package io.github.teonistor.draughts.spring

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import io.github.teonistor.commongaming.{GameConfiguration, GamesHolder, HyperView, Lobby}
import io.github.teonistor.draughts.data.Settings
import io.github.teonistor.draughts.rule.{AvailableMovesRule, GameOverChecker}
import io.github.teonistor.draughts.srlz.{PieceModule, PlayerModule}
import io.github.teonistor.draughts.{Game, InitialBoardProvider, InitialGameProvider}
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import java.util.concurrent.TimeUnit.MINUTES

@Configuration
@EnableWebSocketMessageBroker
class DraughtsConfig {

  /* TODO UI
    * Hover to see coord
    * Highlight last move
    * Don't take unhighlighted clicks
    * Spectator page
    * (in the future) Rotate in any way

    TODO Engine
    * Forced take rule

    Ideas
    * (for all games)
      * Add a link to Github for the respective repos ("Raise an issue if found" etc)
      * Notification when your go (once player assigned and it knows it's you and you weren't paying attention)
      * Full lobby implem, obvs
   */

  @Bean
  def pieceModule() = PieceModule

  @Bean
  def playerModule() = PlayerModule

  @Bean
  def scalaModule() = DefaultScalaModule

  @Bean
  def gamesHolder(hyperView: HyperView[Game], lobby: Lobby) = {
    val initialGameProvider = new InitialGameProvider(new AvailableMovesRule(), new GameOverChecker(), new InitialBoardProvider())
    val gameOfDraughts = GameConfiguration("Draughts", Set("Black","White"))
    new GamesHolder(initialGameProvider.createGame, (10, MINUTES), hyperView, key => lobby.create(key, gameOfDraughts))
  }

  // Some lazy something needed to break circular dependency...
  @Bean
  def gamesHolderGetter(applicationContext: ApplicationContext) =
    () => applicationContext.getBean("gamesHolder", classOf[GamesHolder[Game,Settings]])
}
