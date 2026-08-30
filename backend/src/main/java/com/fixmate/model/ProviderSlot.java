package com.fixmate.model;

import java.time.LocalTime;

public class ProviderSlot {
    private Long providerSlotId;
    private Long providerId;
    private Integer dayOfWeek; // 1=Mon, 7=Sun
    private Long slotId;
    private Boolean isActive;

    // Joined Slot details
    private String slotName;
    private LocalTime startTime;
    private LocalTime endTime;

    public ProviderSlot() {}

    public Long getProviderSlotId() { return providerSlotId; }
    public void setProviderSlotId(Long providerSlotId) { this.providerSlotId = providerSlotId; }
    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }
    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public String getSlotName() { return slotName; }
    public void setSlotName(String slotName) { this.slotName = slotName; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
