package com.leon.controllers;

import com.leon.models.Room;
import com.leon.models.User;
import com.leon.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class UserController
{
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	UserService userService;

	@CrossOrigin
	@RequestMapping(value = "/roomsWithMembership", method={GET}, consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	List<Room> getRoomsWithMembership(String userId)
	{
		if(userId.isEmpty() || userId == null)
		{
			logger.error("userId cannot be null or an empty string when requesting a list of rooms with membership.");
			return new ArrayList<>();
		}

		logger.info("Received request to get list of rooms for which user: " + userId + " has membership.");
		return userService.getRoomsWithMembership(userId);
	}

	@CrossOrigin
	@RequestMapping(value = "/readTimestamps", method={GET}, consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	Map<String, LocalDateTime> getReadTimestamps(String userId)
	{

		if(userId.isEmpty() || userId == null)
		{
			logger.error("userId cannot be null or an empty string when requesting a map of rooms and their read timestamps.");
			return new HashMap<>();
		}

		logger.info("Received request to get map of rooms and their read timestamps for user: " + userId);
		return this.userService.getReadTimestamps(userId);
	}

	@CrossOrigin
	@RequestMapping(value = "/users", method={GET}, consumes= MediaType.APPLICATION_JSON_VALUE)
	List<User> getAllUsers()
	{
		logger.info("Received request to get this list of all users.");
		return this.userService.getAllUsers();
	}

	@CrossOrigin
	@RequestMapping(value = "/addUser", method={POST}, consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	void addUser(String fullName)
	{
		if(fullName.isEmpty() || fullName == null)
		{
			logger.error("fullName cannot be null or an empty string when adding a new user.");
			return;
		}

		logger.info("Received request to add user with full name: " + fullName);
		this.userService.addUser(fullName);
	}
}
