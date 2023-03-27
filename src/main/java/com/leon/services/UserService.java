package com.leon.services;

import com.leon.models.Room;
import com.leon.models.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserService
{
	List<Room> getRoomsWithMembership(String userId);

	Map<String, LocalDateTime> getReadTimestamps(String userId);

	List<User> getAllUsers();

	void addUser(String fullName);

	boolean isValidAuthor(String authorId);

	boolean isValidAuthor(UUID author);
}
