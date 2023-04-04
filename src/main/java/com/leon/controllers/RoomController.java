package com.leon.controllers;

import com.leon.models.*;
import com.leon.services.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
	@RequestMapping(value = "/heartbeat", method={GET})
	ResponseEntity<String> heartbeat()
	{
		return ResponseEntity.ok("I am here!");
	}

	@CrossOrigin
	@RequestMapping(value = "/reload", method={GET})
	ResponseEntity<String> reload()
	{
		this.roomService.reload();
		return ResponseEntity.ok("Successfully reloaded data from store.");
	}

	@CrossOrigin
	@RequestMapping(value = "/addRoom", method={POST}, consumes= MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> addRoom(@RequestBody Room room)
	{
		if(room == null)
		{
			logger.error("room cannot be null when adding a room.");
			return ResponseEntity.badRequest().body("room cannot be null when adding a room.");
		}

		if(room.getRoomName() == null || room.getRoomName().isEmpty())
		{
			logger.error("roomName cannot be null or an empty string when adding a room.");
			return ResponseEntity.badRequest().body("roomName cannot be null or an empty string when adding a room.");
		}

		if(room.getOwnerId() == null)
		{
			logger.error("room owner cannot be null and must be a valid UUID");
			return ResponseEntity.badRequest().body("room owner cannot be null and must be a valid UUID");
		}

		logger.info("Received request to add room: " + room);
		boolean result = this.roomService.addRoom(room);

		if(result)
			return ResponseEntity.ok("Successfully added room with name: " + room.getRoomName() + " that is owned by" + room.getOwnerId());
		else
			return ResponseEntity.badRequest().body("Unable to add room with name: " + room.getRoomName() + " that is owned by: " + room.getOwnerId());
	}

	@CrossOrigin
	@RequestMapping(value = "/rooms", method={GET})
	ResponseEntity<List<UUID>> getAllRooms()
	{
		logger.info("Received request to get list of all rooms.");
		return ResponseEntity.ok(this.roomService.getAllRooms());
	}

	@CrossOrigin
	@RequestMapping(value = "/deactivateRoom", method={PUT})
	ResponseEntity<String> deactivateRoom(@RequestParam String roomId)
	{
		boolean result;
		if(roomId == null || roomId.isEmpty())
			logger.error("roomId cannot be null or an empty string when deactivating a room.");

		logger.info("Received request to deactivate room with ID: " + roomId);
		result = this.roomService.deactivateRoom(roomId);

		if(result)
			return ResponseEntity.ok("Deactivated room with ID: " +  roomId);
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find room with ID: " + roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/conversation", method={GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Conversation> getConversation(@RequestParam String roomId, @RequestParam int startOffset, @RequestParam int endOffset)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when getting the conversation for a room with Id: " + roomId + " and start offset: " + startOffset + " end offset: " + endOffset);
			return ResponseEntity.badRequest().body(new Conversation());
		}

		if(startOffset > endOffset)
		{
			logger.error("Start offset: " + startOffset + " cannot be greater than the end offset: " + endOffset + " for room with Id: " + roomId);
			return ResponseEntity.badRequest().body(new Conversation());
		}

		logger.info("Received request to get the conversation of a room with ID: " + roomId + " with start offset: " + startOffset + " and end offset: " + endOffset);
		Optional<Conversation> result = this.roomService.getConversation(roomId, startOffset, endOffset);

		if(result.isPresent())
			return ResponseEntity.ok(this.roomService.getConversation(roomId, startOffset, endOffset).get());
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Conversation());
	}

	@CrossOrigin
	@RequestMapping(value = "/addChat", method={POST}, consumes= MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> AddChat(@RequestBody ChatMessage chatMessage)
	{
		if(chatMessage == null)
		{
			logger.error("Chat message request body cannot be null when adding a new chat message.");
			return ResponseEntity.badRequest().body("Chat message request body cannot be null when adding a new chat message.");
		}

		if(chatMessage.getAuthorId() == null)
		{
			logger.error("Invalid author ID cannot be used to add new chat message.");
			return ResponseEntity.badRequest().body("Invalid author ID cannot be used to add a new chat message.");
		}

		if(chatMessage.getContent() == null || chatMessage.getContent().isEmpty())
		{
			logger.error("Invalid chat message content cannot be added to add new chat message.");
			return ResponseEntity.badRequest().body("Invalid chat message content cannot be used to add new chat message.");
		}

		if(chatMessage.getRoomId() == null)
		{
			logger.error("Invalid room ID cannot be used to add a new chat message.");
			return ResponseEntity.badRequest().body("Invalid room ID cannot be used to add a new chat message.");
		}

		logger.info("Received request to add chat message: " + chatMessage);
		boolean result = this.roomService.addChat(chatMessage);

		if(result)
			return ResponseEntity.ok("Successfully created new chat message and added to conversation in room with ID: " + chatMessage.getRoomId());
		else
			return ResponseEntity.badRequest().body("Unable to add chat message to the conversation in room with ID: " + chatMessage.getRoomId());
	}

	@CrossOrigin
	@RequestMapping(value = "/members", method={GET})
	ResponseEntity<List<UUID>> getMembers(@RequestParam String roomId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when requesting the list of members of a room.");
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		logger.info("Received request to get the list of members of a room with ID: " + roomId);
		Optional<List<UUID>> result = this.roomService.getMembers(roomId);

		if(result.isPresent())
			return ResponseEntity.ok(result.get());
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
	}

	@CrossOrigin
	@RequestMapping(value = "/admins", method={GET})
	ResponseEntity<List<UUID>>  getAdministrators(@RequestParam String roomId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when getting the list of administrators.");
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		logger.info("Received request to get the list of administrators for a room with ID: " + roomId);
		Optional<List<UUID>> result = this.roomService.getAdministrators(roomId);

		if(result.isPresent())
			return ResponseEntity.ok(result.get());
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
	}

	@CrossOrigin
	@RequestMapping(value = "/memberCount", method={GET} )
	ResponseEntity<Integer> getMemberCount(@RequestParam String roomId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when getting the membership count of a room");
			return ResponseEntity.badRequest().body(0);
		}

		logger.info("Received request to get the membership count for a room with ID: " + roomId);
		int result = this.roomService.getMemberCount(roomId);

		if(result == -1)
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);

		if(result == -2)
			return ResponseEntity.badRequest().body(0);

		return ResponseEntity.ok(result);
	}

	@CrossOrigin
	@RequestMapping(value = "/addMember", method={POST}, consumes= MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> addMember(@RequestParam String roomId, @RequestParam String newMemberId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when adding a new member to a room.");
			return ResponseEntity.badRequest().body("roomId cannot be null or an empty string when adding a new member to a room.");
		}

		if(newMemberId == null || newMemberId.isEmpty())
		{
			logger.error("newMemberId cannot be null or an empty string when adding a new member to a room.");
			return ResponseEntity.badRequest().body("newMemberId cannot be null or an empty string when adding a new member to a room.");
		}

		logger.info("Received request to add a new member with ID: " + newMemberId + " to a room with ID: " + roomId);
		boolean result = this.roomService.addMember(roomId, newMemberId);

		if(result)
			return ResponseEntity.ok("Successfully added new member: " + newMemberId + " to room with ID: " + roomId);
		else
			return ResponseEntity.badRequest().body("Unable to add member: " + newMemberId + " to room with ID: " + roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/removeMember", method={DELETE}, consumes= MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> removeMember(@RequestParam String roomId, @RequestParam String memberId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when removing a member from a room.");
			return ResponseEntity.badRequest().body("roomId cannot be null or an empty string when removing a member from a room.");
		}

		if(memberId == null || memberId.isEmpty())
		{
			logger.error("memberId cannot be null or an empty string when removing a member from a room.");
			return ResponseEntity.badRequest().body("memberId cannot be null or an empty string when removing a member from a room.");
		}

		logger.info("Received request to remove a member with ID: " + memberId + " from a room with ID: " + roomId);
		boolean result = this.roomService.removeMember(roomId, memberId);

		if(result)
			return ResponseEntity.ok("Successfully removed member with ID: " + memberId + " from room with ID: " + roomId);
		else
			return ResponseEntity.badRequest().body("Unable to remove member with ID: " + memberId + " from room with ID: " + roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/addAdmin", method={POST}, consumes= MediaType.APPLICATION_JSON_VALUE )
	ResponseEntity<String> addAdmin(@RequestParam String roomId, @RequestParam String newAdminId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when adding admin to a room.");
			return ResponseEntity.badRequest().body("roomId cannot be null or an empty string when adding admin to a room.");
		}

		if(newAdminId == null || newAdminId.isEmpty())
		{
			logger.error("newAdminId cannot be null or an empty string when adding admin to a room.");
			return ResponseEntity.badRequest().body("newAdminId cannot be null or an empty string when adding admin to a room.");
		}

		logger.info("Received request to add a new admin with ID: " + newAdminId + " to a room with ID: " + roomId);
		boolean result = this.roomService.addAdmin(roomId, newAdminId);

		if(result)
			return ResponseEntity.ok("Successfully added admin with ID: " + newAdminId + " to room with ID: " + roomId);
		else
			return ResponseEntity.badRequest().body("Unable to add admin with ID: " + newAdminId + " to room with ID: " + roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/removeAdmin", method={DELETE}, consumes= MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> removeAdmin(@RequestParam String roomId, @RequestParam String adminId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when removing an admin from a room.");
			return ResponseEntity.badRequest().body("roomId cannot be null or an empty string when removing an admin from a room.");
		}

		if(adminId == null || adminId.isEmpty())
		{
			logger.error("adminId cannot be null or an empty string when removing an admin from a room.");
			return ResponseEntity.badRequest().body("adminId cannot be null or an empty string when removing an admin from a room.");
		}

		logger.info("Received request to remove an admin with ID: " + adminId + " from a room with ID: " + roomId);
		boolean result = this.roomService.removeAdmin(roomId, adminId);

		if(result)
			return ResponseEntity.ok("Successfully removed admin with ID: " + adminId + " from room with ID: " + roomId);
		else
			return ResponseEntity.badRequest().body("Unable to remove admin with ID: " + adminId + " from room with ID: " + roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/roomsWithMembership", method={GET}, consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<UUID>> getRoomsWithMembership(String userId)
	{
		if(userId == null || userId.isEmpty())
		{
			logger.error("userId cannot be null or an empty string when requesting a list of rooms with membership.");
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		logger.info("Received request to get list of rooms for which user: " + userId + " has membership.");
		Optional<List<UUID>> result = roomService.getRoomsWithMembership(userId);

		if(result.isPresent())
			return ResponseEntity.ok(result.get());
		else
			return ResponseEntity.badRequest().body(new ArrayList<>());

	}

	@CrossOrigin
	@RequestMapping(value = "/readTimestamps", method={GET}, consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Map<UUID, LocalDateTime>> getReadTimestamps(@RequestParam String userId)
	{
		if(userId == null || userId.isEmpty())
		{
			logger.error("userId cannot be null or an empty string when requesting a map of rooms and their read timestamps.");
			return ResponseEntity.badRequest().body(new HashMap<>());
		}

		logger.info("Received request to get map of rooms and their read timestamps for user: " + userId);
		Optional<Map<UUID, LocalDateTime>> result = this.roomService.getReadTimestamps(userId);

		if(result.isPresent())
			return ResponseEntity.ok(result.get());
		else
			return ResponseEntity.badRequest().body(new HashMap<>());
	}

	@CrossOrigin
	@RequestMapping(value = "/users", method={GET})
	ResponseEntity<List<User>> getAllUsers()
	{
		logger.info("Received request to get list of all users.");
		return ResponseEntity.ok(this.roomService.getAllUsers());
	}

	@CrossOrigin
	@RequestMapping(value = "/addUser", method={POST})
	ResponseEntity<String> addUser(@RequestParam String fullName)
	{
		if(fullName == null || fullName.isEmpty())
		{
			logger.error("fullName cannot be null or an empty string when adding a new user.");
			return ResponseEntity.badRequest().body("fullName cannot be null or an empty string when adding a new user.");
		}

		logger.info("Received request to add user with full name: " + fullName);
		boolean result = this.roomService.addUser(fullName);

		if(result)
			return ResponseEntity.ok("Successfully added user with full name: " + fullName);
		else
			return ResponseEntity.badRequest().body("Unable to added user with full name: " + fullName);
	}
}
