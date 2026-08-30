package com.fixmate.model;

import java.time.LocalTime;

public class Slot {
    private Long slotId;
    private String slotName;
    private LocalTime startTime;
    private LocalTime endTime;

    public Slot() {}

    public Slot(Long slotId, String slotName, LocalTime startTime, LocalTime endTime) {
        this.slotId = slotId;
        this.slotName = slotName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getSlotId() { return slotId; }
    public void setSlotId(Long slotId) { this.slotId = slotId; }
    public String getSlotName() { return slotName; }
    public void setSlotName(String slotName) { this.slotName = slotName; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
