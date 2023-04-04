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

	boolean removeAdmin(String roomId, String adminId);

	boolean addAdmin(String roomId, String newAdminId);

	boolean addMember(String roomId, String newMemberId);

	boolean removeMember(String roomId, String memberId);

	Optional<List<UUID>> getAdministrators(String roomId);

	Optional<List<UUID>> getMembers(String roomId);

	boolean addChat(ChatMessage chatMessage);

	boolean deactivateRoom(String roomId);

	Optional<Conversation> getConversation(String roomId, int startOffset, int endOffset);

	Optional<List<UUID>> getRoomsWithMembership(String userId);

	Optional<Room> getRoom(String roomId);

	Optional<Map<UUID, LocalDateTime>> getReadTimestamps(String userId);

	List<User> getAllUsers();

	boolean addUser(String fullName);

	List<UUID> getAllRooms();

	void reload();
}
