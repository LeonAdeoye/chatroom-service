package com.leon.controllers;

import com.leon.models.*;
import com.leon.services.RoomService;
import com.leon.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class RoomController
{
	private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

	@Autowired
	RoomService roomService;
	@Autowired
	UserService userService;

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

		if(room.getOwner() == null || room.getOwner().isEmpty())
		{
			logger.error("room owner cannot be null or an empty string when adding a room.");
			return;
		}

		logger.info("Received request to add room: " + room);
		this.roomService.addRoom(room);
	}

	@CrossOrigin
	@RequestMapping(value = "/deactivateRoom", method={PUT}, consumes= MediaType.APPLICATION_JSON_VALUE)
	void deactivateRoom(@RequestParam String roomId)
	{
		if(roomId.isEmpty() || roomId == null)
		{
			logger.error("roomId cannot be null or an empty string when deactivating a room.");
			return;
		}

		logger.info("Received request to deactivate room with ID: " + roomId);
		this.roomService.deactivateRoom(roomId);
	}

	Conversation getConversation(String roomId, int startOffset, int endOffset)
	{
		if(roomId.isEmpty() || roomId == null)
		{
			logger.error("roomId cannot be null or an empty string when getting the conversation for a room with Id: " + roomId + " and start offset: " + startOffset + " end offset: " + endOffset);
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

		// TODO author ID is a UUID. Review
		if(chatMessage.getAuthor() == null)
		{
			logger.error("Invalid author ID used to add chat.");
			return;
		}

		if(chatMessage.getContent() == null || chatMessage.getContent().isEmpty())
		{
			logger.error("Invalid chat message cannot be added to the room's conversation.");
			return;
		}

		// TODO room ID is a UUID. Review.
		if(chatMessage.getRoomId() == null)
		{
			logger.error("Invalid room cannot be used to create chat message.");
			return;
		}

		if(this.userService.isValidAuthor(chatMessage.getAuthor()))
		{
			logger.info("Received request to add chat message: " + chatMessage);
			this.roomService.addChat(chatMessage);
		}
		else
			logger.error("Invalid author with id: " + chatMessage.getAuthor() + " is used to create chat message.");
	}

	@CrossOrigin
	@RequestMapping(value = "/activities", method={GET}, consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	List<Activity> getActivities(@RequestParam String roomId, @RequestParam int startOffset, @RequestParam int endOffset)
	{
		if(roomId.isEmpty() || roomId == null)
		{
			logger.error("roomId cannot be null or an empty string when getting the list of activities for a room with Id: " + roomId + " and start offset: " + startOffset + " end offset: " + endOffset);
			return new ArrayList<>();
		}

		logger.info("Received request to get the list of activities of a room with ID: " + roomId + " with start offset: " + startOffset + " and end offset: " + endOffset);
		return this.roomService.getActivities(roomId, startOffset, endOffset);
	}

	@CrossOrigin
	@RequestMapping(value = "/members", method={GET}, consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	List<UUID> getMembers(@RequestParam String roomId)
	{
		if(roomId.isEmpty() || roomId == null)
		{
			logger.error("roomId cannot be null or an empty string when requesting the list of members of a room.");
			return new ArrayList<>();
		}

		logger.info("Received request to get the list of members of a room with ID: " + roomId);
		return this.roomService.getMembers(roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/admins", method={GET}, consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	List<UUID> getAdministrators(@RequestParam String roomId)
	{
		if(roomId.isEmpty() || roomId == null)
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
		if(roomId.isEmpty() || roomId == null)
		{
			logger.error("roomId cannot be null or an empty string when getting the membership count of a room");
			return 0;
		}

		logger.info("Received request to get the membership count for a room with ID: " + roomId);
		return this.roomService.getMemberCount(roomId);
	}

	@CrossOrigin
	@RequestMapping(value = "/addMember", method={POST}, consumes= MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE )
	void addMember(@RequestParam String roomId, @RequestParam String newMemberId)
	{
		if(roomId.isEmpty() || roomId == null)
		{
			logger.error("roomId cannot be null or an empty string when adding a new member to a room.");
			return;
		}

		if(newMemberId.isEmpty() || newMemberId == null)
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
		if(roomId.isEmpty() || roomId == null)
		{
			logger.error("roomId cannot be null or an empty string when removing a member from a room.");
			return;
		}

		if(memberId.isEmpty() || memberId == null)
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
		if(roomId.isEmpty() || roomId == null)
		{
			logger.error("roomId cannot be null or an empty string when adding admin to a room.");
			return;
		}

		if(newAdminId.isEmpty() || newAdminId == null)
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
		if(roomId.isEmpty() || roomId == null)
		{
			logger.error("roomId cannot be null or an empty string when removing an admin from a room.");
			return;
		}

		if(adminId.isEmpty() || adminId == null)
		{
			logger.error("adminId cannot be null or an empty string when removing an admin from a room.");
			return;
		}

		logger.info("Received request to remove an admin with ID: " + adminId + " from a room with ID: " + roomId);
		this.roomService.removeAdmin(roomId, adminId);
	}
}
