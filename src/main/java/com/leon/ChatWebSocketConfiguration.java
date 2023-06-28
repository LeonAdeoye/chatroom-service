package com.leon;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Enables WebSocket message handling, backed by a message broker.
public class ChatWebSocketConfiguration implements WebSocketMessageBrokerConfigurer
{
	// The method configureMessageBroker() enables a simple memory-based message broker to carry
	// the messages back to the client on destinations prefixed with "/chat-topic".
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry)
	{
		// Set application destination prefix: /chat-app.
		// The client will send messages at this endpoint.
		// For example, if client sends message at /chat-app/message,
		// the endpoint configured at /message in the spring controller will be invoked.
		registry.setApplicationDestinationPrefixes("/chat-app");
		// Enable a simple message broker and configure a prefix to filter destinations targeting the broker.
		// The client app will subscribe messages at endpoints starting with these configured endpoint.
		registry.enableSimpleBroker("/chat-topic");
	}
}