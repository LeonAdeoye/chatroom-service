package com.leon.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties
public class Conversation
{
	@JsonProperty("chatMessages")
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
