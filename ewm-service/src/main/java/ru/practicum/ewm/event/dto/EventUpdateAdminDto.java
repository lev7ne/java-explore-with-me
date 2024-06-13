package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventUpdateAdminDto {
    @Length(min = 3, max = 120)
    private String title;
    @Length(min = 20, max = 2000)
    private String annotation;
    private long categoryId;
    @Length(min = 20, max = 7000)
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private boolean paid;
    private long participantLimit;
    private boolean requestModeration;
    private StateAction stateAction;

    public enum StateAction {
        PUBLISH_EVENT, REJECT_EVENT
    }
}
