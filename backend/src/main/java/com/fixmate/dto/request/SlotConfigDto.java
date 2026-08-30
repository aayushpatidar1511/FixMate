package com.fixmate.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class SlotConfigDto {
    @NotNull(message = "Day of week is required (1=Mon..7=Sun)")
    private Integer dayOfWeek;

    private List<Long> activeSlotIds;

    public SlotConfigDto() {}

    public Integer getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public List<Long> getActiveSlotIds() { return activeSlotIds; }
    public void setActiveSlotIds(List<Long> activeSlotIds) { this.activeSlotIds = activeSlotIds; }
}
