package com.leon;

import com.leon.services.SocketTextHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class ChatWebSocketConfiguration implements WebSocketConfigurer
{
	@Autowired
	private SocketTextHandler socketTextHandler;
	@Value("${client.app.allowed.origins:*}")
	private String allowedOrigins;
	@Value("${handler.destination.from.client.to.server:/stomp}")
	private String handlerDestinationFromClientToServer;
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry)
	{
		webSocketHandlerRegistry.addHandler(socketTextHandler, handlerDestinationFromClientToServer).setAllowedOrigins(allowedOrigins);
	}
}
