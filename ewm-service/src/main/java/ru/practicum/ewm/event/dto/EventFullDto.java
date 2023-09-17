package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
public class EventFullDto {
    private String annotation;
    private NewCategoryDto category;
    private Integer confirmedRequests;
    private LocalDateTime createdOn; // yyyy-MM-dd HH:mm:ss
    private String description;
    private LocalDateTime eventDate; // yyyy-MM-dd HH:mm:ss
    private Long id;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private LocalDateTime publishedOn; // yyyy-MM-dd HH:mm:ss
    private Boolean requestModeration;
    private String state;
    private String title;
    private Long views;
}
