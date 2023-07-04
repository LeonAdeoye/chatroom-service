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
	@JsonProperty("instigatorId")
	private UUID instigatorId;
	@JsonProperty("id")
	private UUID id;

	public UUID getInstigatorId()
	{
		return instigatorId;
	}

	public void setInstigator(UUID instigator)
	{
		this.instigatorId = instigatorId;
	}

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

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}

	public void setTimestamp(LocalDateTime timestamp)
	{
		this.timestamp = timestamp;
	}

	public Activity(ActivityEnum activity, UUID thirdPartyId, UUID instigatorId)
	{
		this.activity = activity;
		this.thirdPartyId = thirdPartyId;
		this.instigatorId = instigatorId;
		this.timestamp = LocalDateTime.now();
		this.id = UUID.randomUUID();
	}

	public Activity()
	{
		this.activity = ActivityEnum.NONE;
		this.thirdPartyId = UUID.randomUUID();
		this.timestamp = LocalDateTime.now();
		this.instigatorId = UUID.randomUUID();
		this.id = UUID.randomUUID();
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Activity otherActivity = (Activity) o;

		return Objects.equals(getId(), otherActivity.getId())
				&& getActivity() == otherActivity.getActivity()
				&& Objects.equals(getThirdPartyId(), otherActivity.getThirdPartyId())
				&& Objects.equals(getTimestamp(), otherActivity.getTimestamp())
				&& Objects.equals(getInstigatorId(), otherActivity.getInstigatorId());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getActivity(), getThirdPartyId(), getTimestamp(), getInstigatorId(), getId());
	}

	@Override
	public String toString()
	{
		return "Activity{" + "activity=" + activity + ", thirdPartyId=" + thirdPartyId + ", timestamp=" + timestamp + ", instigatorId=" + instigatorId + ", id=" + id + '}';
	}
}
