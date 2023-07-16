package io.github.teonistor.draughts.spring

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import io.github.teonistor.draughts.rule.{AvailableMovesRule, GameOverChecker}
import io.github.teonistor.draughts.srlz.{PieceModule, PlayerModule}
import io.github.teonistor.draughts.{InitialBoardProvider, InitialGameProvider, Juncture}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.{EnableWebSocketMessageBroker, StompEndpointRegistry, WebSocketMessageBrokerConfigurer}

@Configuration
@EnableWebSocketMessageBroker
class Config extends WebSocketMessageBrokerConfigurer {

  @Bean
  def pieceModule() = PieceModule

  @Bean
  def playerModule() = PlayerModule

  @Bean
  def scalaModule() = DefaultScalaModule


  @Bean
  def junctureFactory() = new Juncture(
    new InitialGameProvider(new AvailableMovesRule(), new GameOverChecker(), new InitialBoardProvider()).createGame, _)

  // Below here reconsider universality
  override def configureMessageBroker(registry: MessageBrokerRegistry): Unit = {
    registry.enableSimpleBroker("/draughts")
    registry.setApplicationDestinationPrefixes("/draughts")
  }

  override def registerStompEndpoints(registry: StompEndpointRegistry): Unit =
    registry.addEndpoint("/draughts-subscribe")
      .setAllowedOrigins("http://localhost:8080", "http://192.168.1.88:8080", "https://teodor.nistor.uk", "https://teonistor.github.io")
      .withSockJS
      // .setSupressCors(true)  /// Use this if CORS problems perhaps

//  @Bean
//  def webMvcConfigurer = new WebMvcConfigurer() {
//    override def addCorsMappings(registry: CorsRegistry): Unit = {
//      registry.addMapping("/**")
//        .allowCredentials(true)
//        .allowedMethods("*")
//        .allowedOrigins("http://localhost:8080", "http://192.168.1.88:8080", "https://teodor.nistor.uk", "https://teonistor.github.io")
//    }
//  }
}
