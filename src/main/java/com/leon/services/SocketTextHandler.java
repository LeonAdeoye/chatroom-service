package com.leon.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketTextHandler extends TextWebSocketHandler
{
	private List<WebSocketSession> sessions = new ArrayList<>();
	private static final Logger logger = LoggerFactory.getLogger(SocketTextHandler.class);

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception
	{
		sessions.add(session);
		logger.info("Connection established. Added session ID: {}, total count of sessions is now: {}", session.getId(), sessions.size());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception
	{
		logger.info("Connection closed");
		sessions.remove(session);
	}

	public void sendMessageToAllClients(String message) throws IOException
	{
		for (WebSocketSession session : sessions)
		{
			session.sendMessage(new TextMessage(message));
			logger.info("Sent message: {} to session ID: {}", message, session.getId());
		}
	}
}
