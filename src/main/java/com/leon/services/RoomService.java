package com.leon.services;

import com.leon.models.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface RoomService
{
	void addRoom(Room room);

	int getMemberCount(String roomId);

	void removeAdmin(String roomId, String adminId);

	void addAdmin(String roomId, String newAdminId);

	void addMember(String roomId, String newMemberId);

	void removeMember(String roomId, String memberId);

	Optional<List<UUID>> getAdministrators(String roomId);

	Optional<List<UUID>> getMembers(String roomId);

	void addChat(ChatMessage chatMessage);

	boolean deactivateRoom(String roomId);

	Optional<Conversation> getConversation(String roomId, int startOffset, int endOffset);

	List<UUID> getRoomsWithMembership(String userId);

	Room getRoom(String roomId);

	Map<UUID, LocalDateTime> getReadTimestamps(String userId);

	List<User> getAllUsers();

	void addUser(String fullName);

	List<UUID> getAllRooms();
}
