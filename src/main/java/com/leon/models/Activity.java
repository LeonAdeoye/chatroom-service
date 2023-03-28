package com.leon.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@JsonIgnoreProperties
public class Activity
{
	public enum ActivityEnum { ADD_MEMBER, REMOVE_MEMBER, ADD_ADMIN, REMOVE_ADMIN, NONE };
	@JsonProperty("activity")
	private ActivityEnum activity;
	@JsonProperty("thirdPartyId")
	private UUID thirdPartyId;
	@JsonProperty("timeStamp")
	private LocalDateTime timestamp;

	public ActivityEnum getActivity()
	{
		return activity;
	}

	public void setActivity(ActivityEnum activity)
	{
		this.activity = activity;
	}

	public UUID getThirdPartyId()
	{
		return thirdPartyId;
	}

	public void setThirdPartyId(UUID thirdPartyId)
	{
		this.thirdPartyId = thirdPartyId;
	}

	public LocalDateTime getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp)
	{
		this.timestamp = timestamp;
	}

	public Activity(ActivityEnum activity, UUID thirdPartyId)
	{
		this.activity = activity;
		this.thirdPartyId = thirdPartyId;
		this.timestamp = LocalDateTime.now();
	}

	public Activity()
	{
		this.activity = ActivityEnum.NONE;
		this.thirdPartyId = UUID.randomUUID();
		this.timestamp = LocalDateTime.now();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Activity activity1 = (Activity) o;
		return getActivity() == activity1.getActivity() && Objects.equals(getThirdPartyId(), activity1.getThirdPartyId()) && Objects.equals(getTimestamp(), activity1.getTimestamp());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getActivity(), getThirdPartyId(), getTimestamp());
	}

	@Override
	public String toString()
	{
		return "Activity{" + "activity=" + activity + ", thirdPartyId=" + thirdPartyId + ", timestamp=" + timestamp + '}';
	}
}
