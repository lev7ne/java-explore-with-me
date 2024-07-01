package ru.ewm.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.ewm.category.dto.CategoryDto;
import ru.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;


@Getter
@Setter
public class EventShortDto {
    private long id;
    private String title;
    private String annotation;
    private CategoryDto category;
    private UserShortDto initiator;
    private LocalDateTime eventDate;
    private boolean paid;
    private long confirmedRequests;
    private long views;
}
