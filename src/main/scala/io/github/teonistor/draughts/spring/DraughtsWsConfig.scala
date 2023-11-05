package io.github.teonistor.draughts.spring

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.{EnableWebSocketMessageBroker, StompEndpointRegistry, WebSocketMessageBrokerConfigurer}

@Configuration
@EnableWebSocketMessageBroker
class DraughtsWsConfig extends WebSocketMessageBrokerConfigurer {

  // Below here reconsider universality
  override def configureMessageBroker(registry: MessageBrokerRegistry): Unit = {
    registry.enableSimpleBroker("/draughts")
    registry.setApplicationDestinationPrefixes("/draughts")
  }

  override def registerStompEndpoints(registry: StompEndpointRegistry): Unit =
    registry.addEndpoint("/draughts-subscribe")
      .setAllowedOrigins("http://localhost:8080", "http://192.168.1.217:8080", "http://192.168.1.88:8080", "https://teodor.nistor.uk", "https://teonistor.github.io")
      .withSockJS
}
