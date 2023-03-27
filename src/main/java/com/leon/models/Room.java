package com.leon.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room
{
	private UUID id;
	private String roomName;
	private String owner;
	private List<UUID> members;
	private boolean isPrivate;
	private boolean isValid;
	private Conversation conversation;
	private List<UUID> administrators;
	private List<Activity> activities;

	public Conversation getConversation()
	{
		return conversation;
	}

	public void setConversation(Conversation conversation)
	{
		this.conversation = conversation;
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getRoomName()
	{
		return roomName;
	}

	public void setRoomName(String roomName)
	{
		this.roomName = roomName;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner(String owner)
	{
		this.owner = owner;
	}

	public List<UUID> getMembers()
	{
		return members;
	}

	public void setMembers(List<UUID> members)
	{
		this.members = members;
	}

	public boolean isPrivate()
	{
		return isPrivate;
	}

	public void setPrivate(boolean aPrivate)
	{
		isPrivate = aPrivate;
	}

	public boolean isValid()
	{
		return isValid;
	}

	public void setValid(boolean valid)
	{
		isValid = valid;
	}

	public List<UUID> getAdministrators()
	{
		return administrators;
	}

	public void setAdministrators(List<UUID> administrators)
	{
		this.administrators = administrators;
	}

	public void addAdministrator(UUID user)
	{
		this.administrators.add(user);
	}

	public List<Activity> getActivities()
	{
		return activities;
	}

	public void setActivities(List<Activity> activities)
	{
		this.activities = activities;
	}

	public void addMember(UUID user)
	{
		this.members.add(user);
	}

	public Room()
	{
		this.id = UUID.randomUUID();
		this.roomName = "";
		this.owner = "";
		this.members = new ArrayList<>();
		this.isPrivate = false;
		this.isValid = false;
		this.conversation = new Conversation();
		this.administrators = new ArrayList<>();
		this.activities = new ArrayList<>();
	}

	public Room(UUID id, String roomName, String owner, boolean isPrivate, boolean isValid)
	{
		this.id = id;
		this.roomName = roomName;
		this.owner = owner;
		this.members = new ArrayList<>();
		this.isPrivate = isPrivate;
		this.isValid = isValid;
		this.conversation = new Conversation();
		this.administrators = new ArrayList<>();
		this.activities = new ArrayList<>();
	}

	public void addChatMessage(ChatMessage chatMessage)
	{
		this.conversation.addChatMessage(chatMessage);
	}
}
