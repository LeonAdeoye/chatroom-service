package com.leon.services;

import com.leon.models.*;

import java.util.List;
import java.util.UUID;

public interface RoomService
{
	void addRoom(Room room);

	int getMemberCount(String roomId);

	void removeAdmin(String roomId, String adminId);

	void addAdmin(String roomId, String newAdminId);

	void addMember(String roomId, String newMemberId);

	void removeMember(String roomId, String memberId);

	List<UUID> getAdministrators(String roomId);

	List<UUID> getMembers(String roomId);

	List<Activity> getActivities(String roomId, int startOffset, int endOffset);

	void addChat(ChatMessage chatMessage);

	void deactivateRoom(String roomId);

	Conversation getConversation(String roomId, int startOffset, int endOffset);


}
