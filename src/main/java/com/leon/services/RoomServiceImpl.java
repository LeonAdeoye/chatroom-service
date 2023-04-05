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
		logger.info("Loaded " + loadedRooms.size() + " rooms from store");

		if(loadedRooms.size() > 0)
		{
			roomsMap = loadedRooms.stream().collect(Collectors.toMap(Room::getId, Function.identity()));
			logger.info("Created map from list:" + roomsMap.size() + " rooms from store");
		}
		else
		{
			roomsMap = new HashMap<>();
			logger.info("Created empty map as repository list is empty.");
		}

		users = userRepository.findAll();
		logger.info("Loaded " + users.size() + " users from store");
	}

	@Override
	public boolean addRoom(Room room)
	{
		if(roomsMap.containsKey(room.getId()))
		{
			logger.error("Room with room Id: " + room.getId() + " already exists.");
			return false;
		}

		if(users.stream().filter(user -> user.getId().equals(room.getOwnerId())).count() == 0)
		{
			logger.error("Room owner with user Id: " + room.getOwnerId() + " does not exists.");
			return false;
		}

		Room newRoom = new Room(room.getRoomName(), room.getOwnerId());
		newRoom.addAdministrator(room.getOwnerId());
		roomsMap.put(newRoom.getId(), newRoom);
		roomRepository.save(newRoom);
		return true;
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
			existingRoom.getActivities().add(new Activity(Activity.ActivityEnum.REMOVE_ADMIN, adminUUID));
			return true;
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return false;
		}
	}

	@Override
	public boolean addAdmin(String roomId, String newAdminId, String instigatorId)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);
			UUID newAdminUUID = UUID.fromString(newAdminId);

			if (!roomsMap.containsKey(roomUUID))
			{
				logger.error("Room with room Id: " + roomId + " does not exists.");
				return false;
			}

			Room existingRoom = roomsMap.get(roomUUID);
			if (existingRoom.getAdministrators().stream().filter(newAdminId::equals).count() != 0)
			{
				logger.error("Administrator with id: " + newAdminId + " already exists in room with Id: " + roomId);
				return false;
			}

			existingRoom.addAdministrator(newAdminUUID);
			existingRoom.getActivities().add(new Activity(Activity.ActivityEnum.ADD_ADMIN, newAdminUUID));
			return true;
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return false;
		}
	}

	@Override
	public boolean addMember(String roomId, String newMemberId, String instigatorId)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);
			UUID newMemberUUID = UUID.fromString(newMemberId);

			if (roomsMap.containsKey(roomUUID))
			{
				logger.error("Room with room Id: " + roomId + " does not exists.");
				return false;
			}

			Room existingRoom = roomsMap.get(roomUUID);
			if (existingRoom.getMembers().stream().filter(newMemberUUID::equals).count() != 0)
			{
				logger.error("Room with Id: " + roomId + " already has a member with this Id: " + newMemberId);
				return false;
			}

			existingRoom.addMember(newMemberUUID);
			existingRoom.getActivities().add(new Activity(Activity.ActivityEnum.ADD_MEMBER, newMemberUUID));
			return true;
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return false;
		}
	}

	@Override
	public boolean removeMember(String roomId, String memberId, String instigator)
	{
		try
		{
			UUID roomUUID = UUID.fromString(roomId);
			UUID memberUUID = UUID.fromString(memberId);

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
			existingRoom.getActivities().add(new Activity(Activity.ActivityEnum.REMOVE_MEMBER, memberUUID));
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
	public boolean addChat(ChatMessage chatMessage)
	{
		try
		{
			UUID roomUUID = chatMessage.getRoomId();
			if(!roomsMap.containsKey(roomUUID))
			{
				logger.error("Room with room Id: " + roomUUID + " does not exists.");
				return false;
			}

			Room existingRoom = roomsMap.get(roomUUID);
			if(!existingRoom.getMembers().contains(chatMessage.getAuthorId()) && !existingRoom.getAdministrators().contains(chatMessage.getAuthorId()))
			{
				logger.error("Author with Id: " + chatMessage.getAuthorId() + " is not a member or an administrator of room with Id: " + roomUUID);
				return false;
			}

			existingRoom.addChatMessage(chatMessage);
			return true;
		}
		catch(IllegalArgumentException iae)
		{
			logger.error(iae.getMessage());
			return false;
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
	public Optional<Conversation> getConversation(String roomId, int startOffset, int endOffset)
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
	public List<UUID> getAllRooms()
	{
		return roomsMap.keySet().stream().collect(Collectors.toList());
	}

	@Override
	public void reload()
	{
		users.clear();
		roomsMap.clear();
		loadFromStore();
	}

	@Override
	public boolean addUser(String fullName)
	{
		User newUser = new User(fullName);
		this.users.add(newUser);
		userRepository.save(newUser);
		return true;
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
