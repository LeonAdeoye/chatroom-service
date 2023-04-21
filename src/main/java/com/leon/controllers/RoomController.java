package com.leon.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	ResponseEntity<Room> addRoom(@RequestBody Room room)
	{
		if(room == null)
		{
			logger.error("room cannot be null when adding a room.");
			return ResponseEntity.badRequest().body(new Room());
		}

		if(room.getRoomName() == null || room.getRoomName().isEmpty())
		{
			logger.error("roomName cannot be null or an empty string when adding a room.");
			return ResponseEntity.badRequest().body(new Room());
		}

		if(room.getOwnerId() == null)
		{
			logger.error("room owner cannot be null and must be a valid UUID");
			return ResponseEntity.badRequest().body(new Room());
		}

		logger.info("Received request to add room: " + room);
		Optional<Room> result = this.roomService.addRoom(room);

		if(result.isPresent())
			return ResponseEntity.ok(result.get());
		else
			return ResponseEntity.badRequest().body(new Room());
	}

	@CrossOrigin
	@RequestMapping(value = "/room", method={GET})
	ResponseEntity<Room> getRoom(@RequestParam String roomId)
	{
		logger.debug("Received request to get room with ID: " + roomId);
		Optional<Room> result = this.roomService.getRoom(roomId);

 		if(result.isPresent())
			return ResponseEntity.ok(result.get());
	 	else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Room());
	}

	@CrossOrigin
	@RequestMapping(value = "/rooms", method={GET})
	ResponseEntity<List<JsonNode>> getAllRooms()
	{
		logger.debug("Received request to get list of all rooms.");
		ObjectMapper objectMapper = new ObjectMapper();
		List<JsonNode> result = new ArrayList<>();
		for (Map.Entry<UUID, String> entry : this.roomService.getAllRooms().entrySet())
		{
			try
			{
				result.add(objectMapper.readTree("{\"id\": \"" + entry.getKey() + "\", \"name\": \"" + entry.getValue() + "\"}"));
			}
			catch (JsonProcessingException jpe)
			{
				logger.error(jpe.getMessage());
			}
		}

		return ResponseEntity.ok(result);
	}

	@CrossOrigin
	@RequestMapping(value = "/deactivateRoom", method={PUT})
	ResponseEntity<String> deactivateRoom(@RequestParam String roomId, @RequestParam String instigatorId)
	{
		if(roomId == null || roomId.isEmpty())
			logger.error("roomId cannot be null or an empty string when deactivating a room.");

		if(instigatorId == null || instigatorId.isEmpty())
			logger.error("instigatorId cannot be null or an empty string when deactivating a room.");

		logger.info("Received request to deactivate room with ID: " + roomId);
		boolean result = this.roomService.deactivateRoom(roomId, instigatorId);

		if(result)
			return ResponseEntity.ok("Deactivated room with ID: " +  roomId);
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not find room with ID: " + roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/conversation", method={GET}, produces=MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<ChatMessage>> getConversation(@RequestParam String roomId, @RequestParam int startOffset, @RequestParam int endOffset)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when getting the conversation for a room with Id: " + roomId + " and start offset: " + startOffset + " end offset: " + endOffset);
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		if(startOffset > endOffset)
		{
			logger.error("Start offset: " + startOffset + " cannot be greater than the end offset: " + endOffset + " for room with Id: " + roomId);
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		logger.info("Received request to get the conversation of a room with ID: " + roomId + " with start offset: " + startOffset + " and end offset: " + endOffset);
		Optional<List<ChatMessage>> result = this.roomService.getConversation(roomId, startOffset, endOffset);

		if(result.isPresent())
			return ResponseEntity.ok(this.roomService.getConversation(roomId, startOffset, endOffset).get());
		else
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
	}

	@CrossOrigin
	@RequestMapping(value = "/addChat", method={POST}, consumes= MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<ChatMessage>> addChat(@RequestBody ChatMessage chatMessage)
	{
		if(chatMessage == null)
		{
			logger.error("Chat message request body cannot be null when adding a new chat message.");
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		if(chatMessage.getAuthorId() == null)
		{
			logger.error("Invalid author ID cannot be used to add new chat message.");
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		if(chatMessage.getContent() == null || chatMessage.getContent().isEmpty())
		{
			logger.error("Invalid chat message content cannot be added to add new chat message.");
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		if(chatMessage.getRoomId() == null)
		{
			logger.error("Invalid room ID cannot be used to add a new chat message.");
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		logger.info("Received request to add chat message: " + chatMessage);
		Optional<List<ChatMessage>> result = this.roomService.addChat(chatMessage);

		if(result.isPresent())
			return ResponseEntity.ok(result.get());
		else
			return ResponseEntity.badRequest().body(new ArrayList<>());
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
	@RequestMapping(value = "/addMember", method={POST})
	ResponseEntity<Room> addMember(@RequestParam String roomId, @RequestParam String newMemberId, @RequestParam String instigatorId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when adding a new member to a room.");
			return ResponseEntity.badRequest().body(new Room());
		}

		if(newMemberId == null || newMemberId.isEmpty())
		{
			logger.error("newMemberId cannot be null or an empty string when adding a new member to a room.");
			return ResponseEntity.badRequest().body(new Room());
		}

		if(instigatorId == null || instigatorId.isEmpty())
		{
			logger.error("instigatorId cannot be null or an empty string when adding a new member to a room.");
			return ResponseEntity.badRequest().body(new Room());
		}

		logger.info("Received request to add a new member with ID: " + newMemberId + " to a room with ID: " + roomId + " by user with ID: " + instigatorId);

		if(!this.roomService.isValidAdministrator(roomId, instigatorId))
		{
			logger.error("The instigator is not a valid administrator of room Id: " + roomId);
			return ResponseEntity.badRequest().body(new Room());
		}

		Optional<Room> result = this.roomService.addMember(roomId, newMemberId, instigatorId);

		if(result.isPresent())
			return ResponseEntity.ok(result.get());
		else
			return ResponseEntity.badRequest().body(new Room());
	}

	@CrossOrigin
	@RequestMapping(value = "/removeMember", method={DELETE})
	ResponseEntity<String> removeMember(@RequestParam String roomId, @RequestParam String memberId, @RequestParam String instigatorId)
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

		if(instigatorId == null || instigatorId.isEmpty())
		{
			logger.error("instigatorId cannot be null or an empty string when adding a new member to a room.");
			return ResponseEntity.badRequest().body("instigatorId cannot be null or an empty string when adding a new member to a room.");
		}

		logger.info("Received request to remove a member with ID: " + memberId + " from a room with ID: " + roomId);

		if(!this.roomService.isValidAdministrator(roomId, instigatorId))
		{
			logger.error("The instigator is not a valid administrator of room with ID: " + roomId);
			return ResponseEntity.badRequest().body("The instigator is not a valid administrator of room with ID: " + roomId);
		}

		boolean result = this.roomService.removeMember(roomId, memberId, instigatorId);

		if(result)
			return ResponseEntity.ok("Successfully removed member with ID: " + memberId + " from room with ID: " + roomId);
		else
			return ResponseEntity.badRequest().body("Unable to remove member with ID: " + memberId + " from room with ID: " + roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/addAdmin", method={POST})
	ResponseEntity<Room> addAdmin(@RequestParam String roomId, @RequestParam String newAdminId, @RequestParam String instigatorId)
	{
		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when adding admin to a room.");
			return ResponseEntity.badRequest().body(new Room());
		}

		if(newAdminId == null || newAdminId.isEmpty())
		{
			logger.error("newAdminId cannot be null or an empty string when adding admin to a room.");
			return ResponseEntity.badRequest().body(new Room());
		}

		if(instigatorId == null || instigatorId.isEmpty())
		{
			logger.error("instigatorId cannot be null or an empty string when adding a new member to a room.");
			return ResponseEntity.badRequest().body(new Room());
		}

		logger.info("Received request to add a new admin with ID: " + newAdminId + " to a room with ID: " + roomId);

		if(!this.roomService.isValidAdministrator(roomId, instigatorId))
		{
			logger.error("The instigator is not a valid administrator of room with ID: " + roomId);
			return ResponseEntity.badRequest().body(new Room());
		}

		Optional<Room> result = this.roomService.addAdmin(roomId, newAdminId, instigatorId);

		if(result.isPresent())
			return ResponseEntity.ok(result.get());
		else
			return ResponseEntity.badRequest().body(new Room());
	}

	@CrossOrigin
	@RequestMapping(value = "/removeAdmin", method={DELETE}, consumes= MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> removeAdmin(@RequestParam String roomId, @RequestParam String adminId, @RequestParam String instigatorId)
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

		if(instigatorId == null || instigatorId.isEmpty())
		{
			logger.error("instigatorId cannot be null or an empty string when adding a new member to a room.");
			return ResponseEntity.badRequest().body("instigatorId cannot be null or an empty string when adding a new member to a room.");
		}

		logger.info("Received request to remove an admin with ID: " + adminId + " from a room with ID: " + roomId);

		if(!this.roomService.isValidAdministrator(roomId, instigatorId))
		{
			logger.error("The instigator is not a valid administrator of room with ID: " + roomId);
			return ResponseEntity.badRequest().body("The instigator is not a valid administrator of room with ID: " + roomId);
		}

		boolean result = this.roomService.removeAdmin(roomId, adminId, instigatorId);

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
		logger.debug("Received request to get list of all users.");
		return ResponseEntity.ok(this.roomService.getAllUsers());
	}

	@CrossOrigin
	@RequestMapping(value = "/addUser", method={POST})
	ResponseEntity<List<User>> addUser(@RequestParam String fullName)
	{
		if(fullName == null || fullName.isEmpty())
		{
			logger.error("fullName cannot be null or an empty string when adding a new user.");
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		logger.info("Received request to add user with full name: " + fullName);
		Optional<List<User>> users = this.roomService.addUser(fullName);

		if(users.isPresent())
			return ResponseEntity.ok(users.get());
		else
			return ResponseEntity.badRequest().body(new ArrayList<>());
	}

	@CrossOrigin
	@RequestMapping(value = "/closeRoom", method={PUT})
	ResponseEntity<List<UUID>> addToClosedRooms(@RequestParam String userId, @RequestParam  String roomId)
	{
		if(userId == null || userId.isEmpty())
		{
			logger.error("userId cannot be null or an empty string when closing a room.");
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when closing a room.");
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		logger.info("Received request from user with ID: " + userId + " to close a room with ID: " + roomId);
		Optional<List<UUID>> closedRooms = this.roomService.closeRoom(userId, roomId);

		if(closedRooms.isPresent())
			return ResponseEntity.ok(closedRooms.get());
		else
			return ResponseEntity.badRequest().body(new ArrayList<>());
	}

	@CrossOrigin
	@RequestMapping(value = "/addToFavourites", method={PUT})
	ResponseEntity<List<UUID>> addToFavourites(@RequestParam String userId, @RequestParam  String roomId)
	{
		if(userId == null || userId.isEmpty())
		{
			logger.error("userId cannot be null or an empty string when adding a room to a user's favourites.");
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		if(roomId == null || roomId.isEmpty())
		{
			logger.error("roomId cannot be null or an empty string when adding a room to a user's favourites.");
			return ResponseEntity.badRequest().body(new ArrayList<>());
		}

		logger.info("Received request from user with ID: " + userId + " to add a room with ID: " + roomId + " to the user's favourites.");
		Optional<List<UUID>> favouriteRooms = this.roomService.addToFavourites(userId, roomId);

		if(favouriteRooms.isPresent())
			return ResponseEntity.ok(favouriteRooms.get());
		else
			return ResponseEntity.badRequest().body(new ArrayList<>());
	}
}
