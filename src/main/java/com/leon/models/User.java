package com.leon.models;

import java.util.UUID;

public class User
{
	private UUID id;
	private String fullName;
	private boolean isActive;
	private boolean isValid;

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

	public User(UUID id, String fullName, boolean isActive, boolean isValid)
	{
		this.id = id;
		this.fullName = fullName;
		this.isActive = isActive;
		this.isValid = isValid;
	}

	public User(String fullName)
	{
		this.id = UUID.randomUUID();
		this.fullName = fullName;
		this.isActive = true;
		this.isValid = true;
	}

	@Override
	public String toString()
	{
		return "User{" + "id='" + id + '\'' + ", fullName='" + fullName + '\'' + ", isActive='" + isActive + '\'' + ", isValid='" + isValid + '\'' + '}';
	}
}
