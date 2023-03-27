package com.leon.services;

import com.leon.models.Room;

import java.time.LocalDateTime;
import java.util.*;

import com.leon.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceImpl implements UserService
{
	@Autowired
	RoomService roomService;

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	public List<Room> getRoomsWithMembership(String userId)
	{
		UUID userUUID = UUID.fromString(userId);

		return new ArrayList<>();
	}

	public Map<String, LocalDateTime> getReadTimestamps(String userId)
	{
		return new HashMap<>();
	}

	@Override
	public List<User> getAllUsers()
	{
		return new ArrayList<>();
	}

	@Override
	public void addUser(String fullName)
	{

	}

	@Override
	public boolean isValidAuthor(String authorId)
	{
		return true;
	}

	@Override
	public boolean isValidAuthor(UUID authorId) { return true; }
}
