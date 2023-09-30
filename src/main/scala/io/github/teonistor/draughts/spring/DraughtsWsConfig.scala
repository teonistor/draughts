package io.github.teonistor.draughts.spring

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.{EnableWebSocketMessageBroker, StompEndpointRegistry, WebSocketMessageBrokerConfigurer}

@Configuration
@EnableWebSocketMessageBroker
class DraughtsWsConfig extends WebSocketMessageBrokerConfigurer {

  override def registerStompEndpoints(registry: StompEndpointRegistry): Unit =
    registry.addEndpoint("/stomp")
      .setAllowedOrigins("http://localhost:8080", "http://192.168.1.217:8080", "http://192.168.1.88:8080", "https://teodor.nistor.uk", "https://teonistor.github.io")
      .withSockJS
}
