package com.leon.services;

import com.leon.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

@Service
public class RoomServiceImpl implements RoomService
{
	private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);
	private Map<UUID, Room> roomsMap = new HashMap<>();

	@Override
	public void addRoom(Room room)
	{
		if(roomsMap.containsKey(room.getId()))
		{
			logger.error("Room with room Id: " + room.getId() + " already exists.");
		}
		else
		{
			Room newRoom = new Room(UUID.randomUUID(), room.getRoomName(), room.getOwner(), room.isPrivate(), true);
			roomsMap.put(newRoom.getId(), newRoom);
		}
	}

	@Override
	public int getMemberCount(String roomId)
	{
		UUID uuid = UUID.fromString(roomId);

		if(roomsMap.containsKey(uuid))
			return roomsMap.get(uuid).getMembers().size();
		else
			logger.error("Room with room Id: " + roomId + " does not exists.");

		return 0;
	}

	@Override
	public void removeAdmin(String roomId, String adminId)
	{
		UUID roomUUID = UUID.fromString(roomId);
		UUID adminUUID = UUID.fromString(adminId);

		if(roomsMap.containsKey(roomUUID))
		{
			Room existingRoom = roomsMap.get(roomUUID);
			if(existingRoom.getAdministrators().stream().filter(adminId::equals).count() == 0)
				logger.error("Room with Id: " + roomId + " does not have an administrator with Id: " + adminId);
			else
				existingRoom.getAdministrators().removeIf(adminUUID::equals);
		}
		else
			logger.error("Room with room Id: " + roomId + " does not exists.");
	}

	@Override
	public void addAdmin(String roomId, String newAdminId)
	{
		UUID roomUUID = UUID.fromString(roomId);
		UUID newAdminUUID = UUID.fromString(newAdminId);

		if(roomsMap.containsKey(roomUUID))
		{
			Room existingRoom = roomsMap.get(roomUUID);
			if(existingRoom.getAdministrators().stream().filter(newAdminUUID::equals).count() != 0)
				logger.error("Room with Id: " + roomId + " does not have an administrator with Id: " + newAdminId);
			else
				existingRoom.addAdministrator(newAdminUUID);
		}
		else
			logger.error("Room with room Id: " + roomId + " does not exists.");
	}

	@Override
	public void addMember(String roomId, String newMemberId)
	{
		UUID roomUUID = UUID.fromString(roomId);
		UUID newMemberUUID = UUID.fromString(newMemberId);

		if(roomsMap.containsKey(roomUUID))
		{
			Room existingRoom = roomsMap.get(roomUUID);
			if(existingRoom.getMembers().stream().filter(newMemberUUID::equals).count() != 0)
				logger.error("Room with Id: " + roomId + " does not have a member with Id: " + newMemberId);
			else
				existingRoom.addMember(newMemberUUID);
		}
		else
			logger.error("Room with room Id: " + roomId + " does not exists.");
	}

	@Override
	public void removeMember(String roomId, String memberId)
	{
		UUID roomUUID = UUID.fromString(roomId);
		UUID adminUUID = UUID.fromString(memberId);

		if(roomsMap.containsKey(roomUUID))
		{
			Room existingRoom = roomsMap.get(roomUUID);
			if(existingRoom.getMembers().stream().filter(memberId::equals).count() == 0)
				logger.error("Room with Id: " + roomId + " does not have a member with Id: " + memberId);
			else
				existingRoom.getMembers().removeIf(adminUUID::equals);
		}
		else
			logger.error("Room with room Id: " + roomId + " does not exists.");
	}

	@Override
	public List<UUID> getAdministrators(String roomId)
	{
		UUID roomUUID = UUID.fromString(roomId);

		if(roomsMap.containsKey(roomUUID))
			return roomsMap.get(roomUUID).getAdministrators();
		else
			logger.error("Room with room Id: " + roomId + " does not exists.");

		return new ArrayList<>();
	}

	@Override
	public List<UUID> getMembers(String roomId)
	{
		UUID roomUUID = UUID.fromString(roomId);

		if(roomsMap.containsKey(roomUUID))
			return roomsMap.get(roomUUID).getMembers();
		else
			logger.error("Room with room Id: " + roomId + " does not exists.");

		return new ArrayList<>();
	}

	@Override
	public List<Activity> getActivities(String roomId, int startOffset, int endOffset)
	{
		UUID roomUUID = UUID.fromString(roomId);

		// TODO filter between the two offsets.
		if(roomsMap.containsKey(roomUUID))
			return roomsMap.get(roomUUID).getActivities();
		else
			logger.error("Room with room Id: " + roomId + " does not exists.");

		return new ArrayList<>();
	}

	@Override
	public void addChat(ChatMessage chatMessage)
	{
		UUID roomUUID = chatMessage.getRoomId();
		if(roomsMap.containsKey(roomUUID))
		{
			Room existingRoom = roomsMap.get(roomUUID);
			existingRoom.addChatMessage(chatMessage);
		}
		else
			logger.error("Room with room Id: " + roomUUID + " does not exists.");
	}

	@Override
	public void deactivateRoom(String roomId)
	{
		UUID roomUUID = UUID.fromString(roomId);
		if(roomsMap.containsKey(roomUUID))
		{
			Room existingRoom = roomsMap.get(roomUUID);
			existingRoom.setValid(false);
		}
		else
			logger.error("Room with room Id: " + roomUUID + " does not exists.");
	}

	@Override
	public Conversation getConversation(String roomId, int startOffset, int endOffset)
	{
		UUID roomUUID = UUID.fromString(roomId);
		if(roomsMap.containsKey(roomUUID))
		{
			Room existingRoom = roomsMap.get(roomUUID);
			// TODO filter between the two offsets.
			return existingRoom.getConversation();
		}
		else
			logger.error("Room with room Id: " + roomUUID + " does not exists.");

		return new Conversation();
	}
}
