package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventCreateDto {
    @NotBlank(message = "The event title cannot be empty")
    @Length(min = 3, max = 120)
    private String title;
    @NotBlank(message = "The event annotation cannot be empty")
    @Length(min = 20, max = 2000)
    private String annotation;
    private long categoryId;
    @NotBlank(message = "The event description cannot be empty")
    @Length(min = 20, max = 7000)
    private String description;
    @Future
    private LocalDateTime eventDate;
    private Location location;
    private boolean paid = false;
    private int participantLimit = 0;
    private boolean requestModeration = true;
}
