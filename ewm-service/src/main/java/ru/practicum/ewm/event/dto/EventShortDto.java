package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
public class EventShortDto {
    private String annotation;
    private NewCategoryDto category;
    private Integer confirmedRequests;
    private LocalDateTime eventDate; // yyyy-MM-dd HH:mm:ss
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
