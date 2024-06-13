package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventDto {
    private String annotation;
    private CategoryDto category;
    private LocalDateTime createdDate;
    private String description;
    private LocalDateTime eventDate;
    private long id;
    private UserShortDto creator;
    private Location location;
    private boolean paid;
    private long participantLimit;
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private Event.State state;
    private String title;
    @Builder.Default
    private long confirmedRequests = 0L;
    @Builder.Default
    private long views = 0L;
}
