package io.github.teonistor.draughts.spring

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import io.github.teonistor.draughts.rule.{AvailableMovesRule, GameOverChecker}
import io.github.teonistor.draughts.srlz.{PieceModule, PlayerModule}
import io.github.teonistor.draughts.{InitialBoardProvider, InitialGameProvider, Juncture}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker

@Configuration
@EnableWebSocketMessageBroker
class DraughtsConfig {

  /* TODO UI
    - Hover to see coord
    - Highlight last move
    - Highlight possible moves (of piece when selected and pieces themselves unselected when it's not all maybe)
    - Don't take stupid clicks
    - Spectator page
    - Correct board orientation (and option to ratate - not flip!)
    - (in the future) Rotate anything

    TODO General
    - Forced take rule
   */

  @Bean
  def pieceModule() = PieceModule

  @Bean
  def playerModule() = PlayerModule

  @Bean
  def scalaModule() = DefaultScalaModule


  @Bean
  def junctureFactory() = new Juncture(
    new InitialGameProvider(new AvailableMovesRule(), new GameOverChecker(), new InitialBoardProvider()).createGame, _)
}
