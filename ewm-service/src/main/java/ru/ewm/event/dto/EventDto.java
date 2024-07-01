package ru.ewm.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.ewm.category.dto.CategoryDto;
import ru.ewm.event.model.Event;
import ru.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;


@Getter
@Setter
public class EventDto {
    private long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private String description;
    private UserShortDto initiator;
    private LocalDateTime eventDate;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private boolean requestModeration;
    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private Event.State state;
    private long confirmedRequests = 0L;
    private long views = 0L;
}
