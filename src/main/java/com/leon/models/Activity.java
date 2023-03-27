package com.leon.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Activity
{
	public enum ActivityEnum { ADD, REMOVE };
	private ActivityEnum activity;
	private UUID instigatorId;
	private UUID thirdPartyId;
	private LocalDateTime timestamp;
}
