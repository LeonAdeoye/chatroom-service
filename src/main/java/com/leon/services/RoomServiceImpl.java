package com.leon.services;

import com.leon.models.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService
{
	private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);
	private Map<UUID, Room> roomsMap = new HashMap<>();
	private List<User> users = new ArrayList<>();

	@Override
	public void addRoom(Room room)
	{
		if(roomsMap.containsKey(room.getId()))
		{
			logger.error("Room with room Id: " + room.getId() + " already exists.");
			return;
		}

		if(users.stream().filter(user -> user.getId().equals(room.getOwnerId())).count() == 0)
		{
			logger.error("Room owner with user Id: " + room.getOwnerId() + " does not exists.");
			return;
		}

		Room newRoom = new Room(room.getRoomName(), room.getOwnerId());
		newRoom.addAdministrator(room.getOwnerId());
		roomsMap.put(newRoom.getId(), newRoom);
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

		if(!roomsMap.containsKey(roomUUID))
		{
			logger.error("Room with room Id: " + roomId + " does not exists.");
			return;
		}

		Room existingRoom = roomsMap.get(roomUUID);
		if(existingRoom.getAdministrators().stream().filter(adminId::equals).count() == 0)
		{
			logger.error("Room with Id: " + roomId + " does not have an administrator with Id: " + adminId);
			return;
		}

		existingRoom.getAdministrators().removeIf(adminUUID::equals);
		existingRoom.getActivities().add(new Activity(Activity.ActivityEnum.REMOVE_ADMIN, adminUUID));
	}

	@Override
	public void addAdmin(String roomId, String newAdminId)
	{
		UUID roomUUID = UUID.fromString(roomId);
		UUID newAdminUUID = UUID.fromString(newAdminId);

		if(!roomsMap.containsKey(roomUUID))
		{
			logger.error("Room with room Id: " + roomId + " does not exists.");
			return;
		}

		Room existingRoom = roomsMap.get(roomUUID);
		if(existingRoom.getAdministrators().stream().filter(newAdminId::equals).count() != 0)
		{
			logger.error("Administrator with id: " + newAdminId + " already exists in room with Id: " + roomId);
			return;
		}

		existingRoom.addAdministrator(newAdminUUID);
		existingRoom.getActivities().add(new Activity(Activity.ActivityEnum.ADD_ADMIN, newAdminUUID));
	}

	@Override
	public void addMember(String roomId, String newMemberId)
	{
		UUID roomUUID = UUID.fromString(roomId);
		UUID newMemberUUID = UUID.fromString(newMemberId);

		if(roomsMap.containsKey(roomUUID))
		{
			logger.error("Room with room Id: " + roomId + " does not exists.");
			return;
		}

		Room existingRoom = roomsMap.get(roomUUID);
		if(existingRoom.getMembers().stream().filter(newMemberUUID::equals).count() != 0)
		{
			logger.error("Room with Id: " + roomId + " does not have a member with Id: " + newMemberId);
			return;
		}

		existingRoom.addMember(newMemberUUID);
		existingRoom.getActivities().add(new Activity(Activity.ActivityEnum.ADD_MEMBER, newMemberUUID));
	}

	@Override
	public void removeMember(String roomId, String memberId)
	{
		UUID roomUUID = UUID.fromString(roomId);
		UUID memberUUID = UUID.fromString(memberId);

		if(!roomsMap.containsKey(roomUUID))
		{
			logger.error("Room with room Id: " + roomId + " does not exists.");
			return;
		}

		Room existingRoom = roomsMap.get(roomUUID);
		if(existingRoom.getMembers().stream().filter(memberId::equals).count() == 0)
		{
			logger.error("Room with Id: " + roomId + " does not have a member with Id: " + memberId);
			return;
		}

		existingRoom.getMembers().removeIf(memberUUID::equals);
		existingRoom.getActivities().add(new Activity(Activity.ActivityEnum.REMOVE_MEMBER, memberUUID));
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
	public void addChat(ChatMessage chatMessage)
	{
		UUID roomUUID = chatMessage.getRoomId();
		if(!roomsMap.containsKey(roomUUID))
		{
			logger.error("Room with room Id: " + roomUUID + " does not exists.");
			return;
		}

		Room existingRoom = roomsMap.get(roomUUID);
		if(!existingRoom.getMembers().contains(chatMessage.getAuthorId()) && !existingRoom.getAdministrators().contains(chatMessage.getAuthorId()))
		{
			logger.error("Author with Id: " + chatMessage.getAuthorId() + " is not a member or an administrator of room with Id: " + roomUUID);
			return;
		}

		existingRoom.addChatMessage(chatMessage);
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
		if(!roomsMap.containsKey(roomUUID))
		{
			logger.error("Room with room Id: " + roomUUID + " does not exists.");
			return new Conversation();
		}

		// TODO filter between the two offsets.
		Room existingRoom = roomsMap.get(roomUUID);
		return existingRoom.getConversation();
	}

	@Override
	public List<UUID> getRoomsWithMembership(String userId)
	{
		return roomsMap.values().stream()
			.filter(room -> room.getMembers().contains(UUID.fromString(userId)))
			.map(Room::getId)
			.collect(Collectors.toList());
	}

	@Override
	public Room getRoom(String roomId)
	{
		UUID roomUUID = UUID.fromString(roomId);
		if(roomsMap.containsKey(roomUUID))
			return roomsMap.get(roomUUID);
		else
		{
			logger.error("Room with ID: " + roomId + " does not exist.");
			return new Room();
		}
	}

	@Override
	public Map<UUID, LocalDateTime> getReadTimestamps(String userId)
	{
		return new HashMap<>();
	}

	@Override
	public List<User> getAllUsers()
	{
		return this.users;
	}

	@Override
	public List<UUID> getAllRooms()
	{
		return roomsMap.keySet().stream().collect(Collectors.toList());
	}

	@Override
	public void addUser(String fullName)
	{
		this.users.add(new User(fullName));
	}
}
