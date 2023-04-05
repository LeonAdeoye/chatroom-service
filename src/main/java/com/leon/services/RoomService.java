package com.leon.services;

import com.leon.models.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface RoomService
{
	boolean addRoom(Room room);

	int getMemberCount(String roomId);

	boolean removeAdmin(String roomId, String adminId, String instigatorId);

	boolean addAdmin(String roomId, String newAdminId, String instigatorId);

	boolean addMember(String roomId, String newMemberId, String instigatorId);

	boolean removeMember(String roomId, String memberId, String instigatorId);

	Optional<List<UUID>> getAdministrators(String roomId);

	Optional<List<UUID>> getMembers(String roomId);

	boolean addChat(ChatMessage chatMessage);

	boolean deactivateRoom(String roomId, String instigatorId);

	Optional<Conversation> getConversation(String roomId, int startOffset, int endOffset);

	Optional<List<UUID>> getRoomsWithMembership(String userId);

	Optional<Room> getRoom(String roomId);

	Optional<Map<UUID, LocalDateTime>> getReadTimestamps(String userId);

	List<User> getAllUsers();

	boolean addUser(String fullName);

	List<UUID> getAllRooms();

	void reload();

	boolean isValidAdministrator(String roomId, String userId);
}
