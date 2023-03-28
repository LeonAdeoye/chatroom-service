package com.leon.controllers;

import com.leon.models.*;
import com.leon.services.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class RoomController
{
	private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

	@Autowired
	RoomService roomService;

	@CrossOrigin
	@RequestMapping("/heartbeat")
	String heartbeat()
	{
		return "Here I am";
	}

	@CrossOrigin
	@RequestMapping(value = "/addRoom", method={POST}, consumes= MediaType.APPLICATION_JSON_VALUE)
	void addRoom(@RequestBody Room room)
	{
		if(room == null)
		{
			logger.error("room cannot be null when adding a room.");
			return;
		}

		if(room.getRoomName() == null || room.getRoomName().isEmpty())
		{
			logger.error("roomName cannot be null or an empty string when adding a room.");
			return;
		}

		if(room.getOwnerId() == null)
		{
			logger.error("room owner cannot be null and must be a valid UUID");
			return;
		}

		logger.info("Received request to add room: " + room);
		this.roomService.addRoom(room);
	}

	@CrossOrigin
	@RequestMapping(value = "/rooms", method={GET})
	List<UUID> getAllRooms()
	{
		logger.info("Received request to get list of all rooms.");
		return this.roomService.getAllRooms();
	}

	@CrossOrigin
	@RequestMapping(value = "/deactivateRoom", method={PUT}, consumes= MediaType.APPLICATION_JSON_VALUE)
	void deactivateRoom(@RequestParam String roomId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when deactivating a room.");
			return;
		}

		logger.info("Received request to deactivate room with ID: " + roomId);
		this.roomService.deactivateRoom(roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/conversation", method={GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	Conversation getConversation(@RequestParam String roomId, @RequestParam int startOffset, @RequestParam int endOffset)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when getting the conversation for a room with Id: " + roomId + " and start offset: " + startOffset + " end offset: " + endOffset);
			return new Conversation();
		}

		if(startOffset > endOffset)
		{
			logger.error("Start offset: " + startOffset + " cannot be greater than the end offset: " + endOffset + " for room with Id: " + roomId);
			return new Conversation();
		}

		logger.info("Received request to get the conversation of a room with ID: " + roomId + " with start offset: " + startOffset + " and end offset: " + endOffset);
		return this.roomService.getConversation(roomId, startOffset, endOffset);
	}

	@CrossOrigin
	@RequestMapping(value = "/addChat", method={POST}, consumes= MediaType.APPLICATION_JSON_VALUE)
	void AddChat(@RequestBody ChatMessage chatMessage)
	{
		if(chatMessage == null)
		{
			logger.error("chat message request body cannot be null when adding a new chat message.");
			return;
		}

		if(chatMessage.getAuthorId() == null)
		{
			logger.error("Invalid author ID used to add chat.");
			return;
		}

		if(chatMessage.getContent() == null || chatMessage.getContent().isEmpty())
		{
			logger.error("Invalid chat message cannot be added to the room's conversation.");
			return;
		}

		if(chatMessage.getRoomId() == null)
		{
			logger.error("Invalid room cannot be used to create chat message.");
			return;
		}

		logger.info("Received request to add chat message: " + chatMessage);
		this.roomService.addChat(chatMessage);
	}

	@CrossOrigin
	@RequestMapping(value = "/members", method={GET})
	List<UUID> getMembers(@RequestParam String roomId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when requesting the list of members of a room.");
			return new ArrayList<>();
		}

		logger.info("Received request to get the list of members of a room with ID: " + roomId);
		return this.roomService.getMembers(roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/admins", method={GET})
	List<UUID> getAdministrators(@RequestParam String roomId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when getting the list of administrators.");
			return new ArrayList<>();
		}

		logger.info("Received request to get the list of administrators for a room with ID: " + roomId);
		return this.roomService.getAdministrators(roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/memberCount", method={GET} )
	int getMemberCount(@RequestParam String roomId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when getting the membership count of a room");
			return 0;
		}

		logger.info("Received request to get the membership count for a room with ID: " + roomId);
		return this.roomService.getMemberCount(roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/addMember", method={POST}, consumes= MediaType.APPLICATION_JSON_VALUE)
	void addMember(@RequestParam String roomId, @RequestParam String newMemberId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when adding a new member to a room.");
			return;
		}

		if(newMemberId == null || newMemberId.isEmpty())
		{
			logger.error("newMemberId cannot be null or an empty string when adding a new member to a room.");
			return;
		}

		logger.info("Received request to add a new member with ID: " + newMemberId + " to a room with ID: " + roomId);
		this.roomService.addMember(roomId, newMemberId);
	}

	@CrossOrigin
	@RequestMapping(value = "/removeMember", method={DELETE}, consumes= MediaType.APPLICATION_JSON_VALUE)
	void removeMember(@RequestParam String roomId, @RequestParam String memberId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when removing a member from a room.");
			return;
		}

		if(memberId == null || memberId.isEmpty())
		{
			logger.error("memberId cannot be null or an empty string when removing a member from a room.");
			return;
		}

		logger.info("Received request to remove a member with ID: " + memberId + " from a room with ID: " + roomId);
		this.roomService.removeMember(roomId, memberId);
	}

	@CrossOrigin
	@RequestMapping(value = "/addAdmin", method={POST}, consumes= MediaType.APPLICATION_JSON_VALUE )
	void addAdmin(@RequestParam String roomId, @RequestParam String newAdminId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when adding admin to a room.");
			return;
		}

		if(newAdminId == null || newAdminId.isEmpty())
		{
			logger.error("newAdminId cannot be null or an empty string when adding admin to a room.");
			return;
		}

		logger.info("Received request to add a new admin with ID: " + newAdminId + " to a room with ID: " + roomId);
		this.roomService.addAdmin(roomId, newAdminId);
	}

	@CrossOrigin
	@RequestMapping(value = "/removeAdmin", method={DELETE}, consumes= MediaType.APPLICATION_JSON_VALUE)
	void removeAdmin(@RequestParam String roomId, @RequestParam String adminId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when removing an admin from a room.");
			return;
		}

		if(adminId == null || adminId.isEmpty())
		{
			logger.error("adminId cannot be null or an empty string when removing an admin from a room.");
			return;
		}

		logger.info("Received request to remove an admin with ID: " + adminId + " from a room with ID: " + roomId);
		this.roomService.removeAdmin(roomId, adminId);
	}

	@CrossOrigin
	@RequestMapping(value = "/roomsWithMembership", method={GET}, consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	List<UUID> getRoomsWithMembership(String userId)
	{
		if(userId == null || userId.isEmpty())
		{
			logger.error("userId cannot be null or an empty string when requesting a list of rooms with membership.");
			return new ArrayList<>();
		}

		logger.info("Received request to get list of rooms for which user: " + userId + " has membership.");
		return roomService.getRoomsWithMembership(userId);
	}

	@CrossOrigin
	@RequestMapping(value = "/readTimestamps", method={GET}, consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	Map<UUID, LocalDateTime> getReadTimestamps(@RequestParam String userId)
	{
		if(userId == null || userId.isEmpty())
		{
			logger.error("userId cannot be null or an empty string when requesting a map of rooms and their read timestamps.");
			return new HashMap<>();
		}

		logger.info("Received request to get map of rooms and their read timestamps for user: " + userId);
		return this.roomService.getReadTimestamps(userId);
	}

	@CrossOrigin
	@RequestMapping(value = "/users", method={GET})
	List<User> getAllUsers()
	{
		logger.info("Received request to get list of all users.");
		return this.roomService.getAllUsers();
	}

	@CrossOrigin
	@RequestMapping(value = "/addUser", method={POST})
	void addUser(@RequestParam String fullName)
	{
		if(fullName == null || fullName.isEmpty())
		{
			logger.error("fullName cannot be null or an empty string when adding a new user.");
			return;
		}

		logger.info("Received request to add user with full name: " + fullName);
		this.roomService.addUser(fullName);
	}
}
