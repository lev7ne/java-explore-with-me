package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventShortDto {
    private long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private UserShortDto creator;
    private LocalDateTime eventDate;
    private boolean paid;
    private long confirmedRequests;
    private long views;
}
