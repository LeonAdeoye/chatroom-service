package com.leon.services;

import com.leon.models.*;
import com.leon.repositories.RoomRepository;
import com.leon.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.Optional.of;

@Service
public class RoomServiceImpl implements RoomService
{
	private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);

	@Autowired
	RoomRepository roomRepository;
	@Autowired
	UserRepository userRepository;

	private Map<UUID, Room> roomsMap;
	private List<User> users;

	@PostConstruct
	public void initialize()
	{
		loadFromStore();
	}

	private void loadFromStore()
	{
		List<Room> loadedRooms = roomRepository.findAll();
		logger.info("Loaded " + loadedRooms.size() + " room(s) from store.");

		if(loadedRooms.size() > 0)
			roomsMap = loadedRooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
		else
			roomsMap = new HashMap<>();

		users = userRepository.findAll();
		logger.info("Loaded " + users.size() + " user(s) from store.");
	}

	@Override
	public Optional<Room> addRoom(Room room)
	{
		if(roomsMap.containsKey(room.getId()))
		{
			logger.error("Room with room Id: " + room.getId() + " already exists.");
			return Optional.empty();
		}

		if(users.stream().filter(user -> user.getId().equals(room.getOwnerId())).count() == 0)
		{
			logger.error("Room owner with user Id: " + room.getOwnerId() + " does not exists.");
			return Optional.empty();
		}

		try
		{
			Room newRoom = new Room(room.getRoomName(), room.getOwnerId());
			newRoom.addAdministrator(room.getOwnerId());
			roomsMap.put(newRoom.getId(), newRoom);
			roomRepository.save(newRoom);
			return Optional.of(newRoom);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
		return Optional.empty();
	}

	@Override
	public int getMemberCount(String roomId)
	{
		try
		{
			UUID uuid = UUID.fromString(roomId);
			if (roomsMap.containsKey(uuid))
				return roomsMap.get(uuid).getMembers().size();
			else
				logger.error("Room with room Id: " + roomId + " does not exists.");
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return -2;
		}

		return -1;
	}

	@Override
	public boolean removeAdmin(String roomId, String adminId, String instigatorId)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);
			UUID adminUUID = UUID.fromString(adminId);
			UUID instigatorUUID = UUID.fromString(instigatorId);

			if (!roomsMap.containsKey(roomUUID))
			{
				logger.error("Room with room Id: " + roomId + " does not exists.");
				return false;
			}

			Room existingRoom = roomsMap.get(roomUUID);
			if (existingRoom.getAdministrators().stream().filter(adminId::equals).count() == 0)
			{
				logger.error("Room with Id: " + roomId + " does not have an administrator with Id: " + adminId);
				return false;
			}

			existingRoom.getAdministrators().removeIf(adminUUID::equals);
			existingRoom.getActivities().add(new Activity(Activity.ActivityEnum.REMOVE_ADMIN, adminUUID, instigatorUUID));
			roomRepository.save(existingRoom);
			return true;
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return false;
		}
	}

	@Override
	public Optional<Room> addAdmin(String roomId, String newAdminId, String instigatorId)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);
			UUID newAdminUUID = UUID.fromString(newAdminId);
			UUID instigatorUUID = UUID.fromString(instigatorId);

			if (!roomsMap.containsKey(roomUUID))
			{
				logger.error("Room with room Id: " + roomId + " does not exists.");
				return Optional.empty();
			}

			Room existingRoom = roomsMap.get(roomUUID);
			if (existingRoom.getAdministrators().stream().filter(newAdminId::equals).count() != 0)
			{
				logger.error("Administrator with id: " + newAdminId + " already exists in room with Id: " + roomId);
				return Optional.empty();
			}

			existingRoom.addAdministrator(newAdminUUID);
			existingRoom.getActivities().add(new Activity(Activity.ActivityEnum.ADD_ADMIN, newAdminUUID, instigatorUUID));
			roomRepository.save(existingRoom);
			return Optional.of(existingRoom);
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public Optional<Room> addMember(String roomId, String newMemberId, String instigatorId)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);
			UUID newMemberUUID = UUID.fromString(newMemberId);
			UUID instigatorUUID = UUID.fromString(instigatorId);

			if (!roomsMap.containsKey(roomUUID))
			{
				logger.error("Room with room Id: " + roomId + " does not exists.");
				return Optional.empty();
			}

			Room existingRoom = roomsMap.get(roomUUID);
			if (existingRoom.getMembers().stream().filter(newMemberUUID::equals).count() != 0)
			{
				logger.error("Room with Id: " + roomId + " already has a member with this Id: " + newMemberId);
				return Optional.empty();
			}

			existingRoom.addMember(newMemberUUID);
			existingRoom.getActivities().add(new Activity(Activity.ActivityEnum.ADD_MEMBER, newMemberUUID, instigatorUUID));
			roomRepository.save(existingRoom);
			return Optional.of(existingRoom);
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public boolean removeMember(String roomId, String memberId, String instigatorId)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);
			UUID memberUUID = UUID.fromString(memberId);
			UUID instigatorUUID = UUID.fromString(instigatorId);

			if (!roomsMap.containsKey(roomUUID))
			{
				logger.error("Room with room Id: " + roomId + " does not exists.");
				return false;
			}

			Room existingRoom = roomsMap.get(roomUUID);
			if (existingRoom.getMembers().stream().filter(memberId::equals).count() == 0)
			{
				logger.error("Room with Id: " + roomId + " does not have a member with Id: " + memberId);
				return false;
			}

			existingRoom.getMembers().removeIf(memberUUID::equals);
			existingRoom.getActivities().add(new Activity(Activity.ActivityEnum.REMOVE_MEMBER, memberUUID, instigatorUUID));
			roomRepository.save(existingRoom);
			return true;
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return false;
		}
	}

	@Override
	public Optional<List<UUID>> getAdministrators(String roomId)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);

			if (roomsMap.containsKey(roomUUID))
				return Optional.of(roomsMap.get(roomUUID).getAdministrators());
			else
				logger.error("Room with room Id: " + roomId + " does not exists.");
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
		}

		return Optional.empty();
	}

	@Override
	public Optional<List<UUID>> getMembers(String roomId)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);

			if(roomsMap.containsKey(roomUUID))
				return Optional.of(roomsMap.get(roomUUID).getMembers());
			else
				logger.error("Room with room Id: " + roomId + " does not exists.");
		}
		catch (IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
		}

		return Optional.empty();
	}

	@Override
	public Optional<List<ChatMessage>> addChat(ChatMessage chatMessage)
	{
		try
		{
			UUID roomUUID = chatMessage.getRoomId();
			if(!roomsMap.containsKey(roomUUID))
			{
				logger.error("Room with room Id: " + roomUUID + " does not exists.");
				return Optional.empty();
			}

			Room existingRoom = roomsMap.get(roomUUID);
			if(!existingRoom.getMembers().contains(chatMessage.getAuthorId()) && !existingRoom.getAdministrators().contains(chatMessage.getAuthorId()))
			{
				logger.error("Author with Id: " + chatMessage.getAuthorId() + " is not a member or an administrator of room with Id: " + roomUUID);
				return Optional.empty();
			}

			existingRoom.addChatMessage(chatMessage);
			roomRepository.save(existingRoom);
			return Optional.of(existingRoom.getConversation());
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public boolean deactivateRoom(String roomId, String instigatorId)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);
			if(roomsMap.containsKey(roomUUID))
			{
				Room existingRoom = roomsMap.get(roomUUID);
				existingRoom.setValid(false);
				return true;
			}
			else
				logger.error("Room with room Id: " + roomUUID + " does not exists.");
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
		}
		return false;
	}

	@Override
	public Optional<List<ChatMessage>> getConversation(String roomId, int startOffset, int endOffset)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);
			if(roomsMap.containsKey(roomUUID))
			{
				// TODO filter between the two offsets.
				Room existingRoom = roomsMap.get(roomUUID);
				return of(existingRoom.getConversation());
			}
			else
				logger.error("Room with room Id: " + roomUUID + " does not exists.");
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
		}
		return Optional.empty();
	}

	@Override
	public Optional<List<UUID>> getRoomsWithMembership(String userId)
	{
		try
		{
			return Optional.of(roomsMap.values().stream()
					.filter(room -> room.getMembers().contains(UUID.fromString(userId)))
					.map(Room::getId)
					.collect(Collectors.toList()));
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public Optional<Room> getRoom(String roomId)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);
			if (roomsMap.containsKey(roomUUID))
				return Optional.of(roomsMap.get(roomUUID));
			else
			{
				logger.error("Room with ID: " + roomId + " does not exist.");
				return Optional.empty();
			}
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public Optional<Map<UUID, LocalDateTime>> getReadTimestamps(String userId)
	{
		try
		{
			// TODO
			return Optional.of(new HashMap<>());
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public List<User> getAllUsers()
	{
		return this.users;
	}

	@Override
	public Map<UUID, String> getAllRooms()
	{
		return this.roomsMap.entrySet().stream()
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().getRoomName()));
	}

	@Override
	public void reload()
	{
		users.clear();
		roomsMap.clear();
		loadFromStore();
	}

	@Override
	public Optional<User> addUser(String fullName)
	{
		try
		{
			User newUser = new User(fullName);
			this.users.add(newUser);
			userRepository.save(newUser);
			return Optional.of(newUser);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
		}
		return Optional.empty();
	}

	@Override
	public boolean isValidAdministrator(String roomId, String userId)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);
			UUID userUUID = UUID.fromString(userId);
			if(roomsMap.containsKey(roomUUID))
			{
				if(roomsMap.get(roomUUID).getAdministrators().stream().filter(userUUID::equals).count() != 0)
					return true;
			}
			else
				logger.error("Room with room Id: " + roomUUID + " does not exists.");
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
		}
		return false;
	}
}
