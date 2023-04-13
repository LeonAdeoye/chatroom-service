package com.leon.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User
{
	private UUID id;
	private String fullName;
	private boolean isActive;
	private boolean isValid;
	private List<UUID> favouriteRooms;
	private List<UUID> closedRooms;

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public boolean getIsActive()
	{
		return isActive;
	}

	public void setIsActive(boolean isActive)
	{
		this.isActive = isActive;
	}

	public boolean getIsValid()
	{
		return isValid;
	}

	public void setIsValid(boolean isValid)
	{
		this.isValid = isValid;
	}

	public List<UUID> getFavouriteRooms()
	{
		return favouriteRooms;
	}

	public void addToFavouriteRooms(UUID favouriteRoomId)
	{
		this.favouriteRooms.add(favouriteRoomId);
	}

	public List<UUID> getClosedRooms()
	{
		return closedRooms;
	}

	public void addToClosedRooms(UUID closedRoomId)
	{
		this.closedRooms.add(closedRoomId);
	}

	public User(UUID id, String fullName, boolean isActive, boolean isValid)
	{
		this.id = id;
		this.fullName = fullName;
		this.isActive = isActive;
		this.isValid = isValid;
		this.favouriteRooms = new ArrayList<>();
		this.closedRooms = new ArrayList<>();
	}

	public User(String fullName)
	{
		this.id = UUID.randomUUID();
		this.fullName = fullName;
		this.isActive = true;
		this.isValid = true;
		this.favouriteRooms = new ArrayList<>();
		this.closedRooms = new ArrayList<>();
	}

	public User()
	{
		this.id = UUID.randomUUID();
		this.fullName = "";
		this.isActive = true;
		this.isValid = true;
		this.favouriteRooms = new ArrayList<>();
		this.closedRooms = new ArrayList<>();
	}

	@Override
	public String toString()
	{
		return "User{" + "id=" + id + ", fullName='" + fullName + '\'' + ", isActive=" + isActive + ", isValid=" + isValid + ", favouriteRooms=" + favouriteRooms + ", closedRooms=" + closedRooms + '}';
	}
}
