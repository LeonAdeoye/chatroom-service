package com.leon.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room
{
	private UUID id;
	private String roomName;
	private UUID ownerId;
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

	public UUID getOwnerId()
	{
		return ownerId;
	}

	public void setOwnerId(UUID ownerId)
	{
		this.ownerId = ownerId;
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
		this.ownerId = UUID.randomUUID();
		this.members = new ArrayList<>();
		this.isPrivate = false;
		this.isValid = false;
		this.conversation = new Conversation();
		this.administrators = new ArrayList<>();
		this.activities = new ArrayList<>();
	}

	public Room(UUID id, String roomName, UUID owner, boolean isPrivate, boolean isValid)
	{
		this.id = id;
		this.roomName = roomName;
		this.ownerId = owner;
		this.members = new ArrayList<>();
		this.isPrivate = isPrivate;
		this.isValid = isValid;
		this.conversation = new Conversation();
		this.administrators = new ArrayList<>();
		this.activities = new ArrayList<>();
	}

	public Room(String roomName, UUID owner)
	{
		this.id = UUID.randomUUID();
		this.roomName = roomName;
		this.ownerId = owner;
		this.members = new ArrayList<>();
		this.isPrivate = false;
		this.isValid = true;
		this.conversation = new Conversation();
		this.administrators = new ArrayList<>();
		this.activities = new ArrayList<>();
	}

	@Override
	public String toString()
	{
		return "Room{" + "id=" + id + ", roomName='" + roomName + '\'' + ", ownerId='" + ownerId + '\'' + ", members=" + members + ", isPrivate=" + isPrivate + ", isValid=" + isValid + ", conversation=" + conversation + ", administrators=" + administrators + ", activities=" + activities + '}';
	}

	public void addChatMessage(ChatMessage chatMessage)
	{
		this.conversation.addChatMessage(chatMessage);
	}
}
