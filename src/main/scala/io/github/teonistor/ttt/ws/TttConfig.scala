package io.github.teonistor.ttt.ws

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.{EnableWebSocketMessageBroker, StompEndpointRegistry, WebSocketMessageBrokerConfigurer}

@Configuration
@EnableWebSocketMessageBroker
class TttConfig extends WebSocketMessageBrokerConfigurer {

  override def configureMessageBroker(config: MessageBrokerRegistry): Unit = {
    config.enableSimpleBroker("/topic")
    config.setApplicationDestinationPrefixes("/app")
  }

  override def registerStompEndpoints(registry: StompEndpointRegistry): Unit = {
    registry.addEndpoint("/chat").withSockJS
  }
}
