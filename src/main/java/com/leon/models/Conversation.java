package com.leon.models;

import java.util.ArrayList;
import java.util.List;

public class Conversation
{
	private List<ChatMessage> chatMessages;

	public void addChatMessage(ChatMessage chatMessage)
	{
		this.chatMessages.add(chatMessage);
	}

	public Conversation()
	{
		this.chatMessages = new ArrayList<>();
	}
}
