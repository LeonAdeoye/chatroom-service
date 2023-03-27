package com.leon.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatMessage
{
	private UUID id;
	private UUID author;
	private LocalDateTime timeStamp;
	private String content;
	private UUID roomId;

	public UUID getRoomId()
	{
		return roomId;
	}

	public void setRoomId(UUID roomId)
	{
		this.roomId = roomId;
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public UUID getAuthor()
	{
		return author;
	}

	public void setAuthor(UUID author)
	{
		this.author = author;
	}

	public LocalDateTime getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
}
