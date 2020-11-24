package io.github.teonistor.ttt.ws

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.{EnableWebSocketMessageBroker, StompEndpointRegistry, WebSocketMessageBrokerConfigurer}

@Configuration
@EnableWebSocketMessageBroker
class TttConfig extends WebSocketMessageBrokerConfigurer {

  override def configureMessageBroker(config: MessageBrokerRegistry) {
    config.enableSimpleBroker("/ttt")
    config.setApplicationDestinationPrefixes("/ttt")
  }

  override def registerStompEndpoints(registry: StompEndpointRegistry) {
    registry.addEndpoint("/ttt-subscribe").withSockJS
  }
}
