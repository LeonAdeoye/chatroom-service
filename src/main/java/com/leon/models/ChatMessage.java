package com.leon.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@JsonIgnoreProperties
public class ChatMessage
{
	@JsonProperty("id")
	private UUID id;
	@JsonProperty("authorId")
	private UUID authorId;
	@JsonProperty("timeStamp")
	private LocalDateTime timeStamp;
	@JsonProperty("content")
	private String content;
	@JsonProperty("roomId")
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

	public UUID getAuthorId()
	{
		return authorId;
	}

	public void setAuthorId(UUID authorId)
	{
		this.authorId = authorId;
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

	public ChatMessage(UUID authorId, String content, UUID roomId)
	{
		this.id = UUID.randomUUID();
		this.authorId = authorId;
		this.timeStamp = LocalDateTime.now();
		this.content = content;
		this.roomId = roomId;
	}

	public ChatMessage()
	{
		this.id = UUID.randomUUID();
		this.authorId = UUID.randomUUID();
		this.timeStamp = LocalDateTime.now();
		this.content = "";
		this.roomId = UUID.randomUUID();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ChatMessage that = (ChatMessage) o;
		return Objects.equals(getId(), that.getId()) && Objects.equals(getAuthorId(), that.getAuthorId()) && Objects.equals(getTimeStamp(), that.getTimeStamp()) && Objects.equals(getContent(), that.getContent()) && Objects.equals(getRoomId(), that.getRoomId());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getId(), getAuthorId(), getTimeStamp(), getContent(), getRoomId());
	}

	@Override
	public String toString()
	{
		return "ChatMessage{" + "id=" + id + ", authorId=" + authorId + ", timeStamp=" + timeStamp + ", content='" + content + '\'' + ", roomId=" + roomId + '}';
	}

}
