package ru.ewm.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;
import ru.ewm.util.annotation.MainServiceAnnotation.NoEarlierThan2HoursBefore;

import java.time.LocalDateTime;


@Getter
@Setter
public class EventCreateDto {
    @NotBlank(message = "The event title cannot be empty")
    @Size(min = 3)
    @Size(max = 120)
    private String title;
    @NotBlank(message = "The event annotation cannot be empty")
    @Size(min = 20)
    @Size(max = 2000)
    private String annotation;
    private long category;
    @NotBlank(message = "The event description cannot be empty")
    @Size(min = 20)
    @Size(max = 7000)
    private String description;
    @Future
    @NoEarlierThan2HoursBefore
    private LocalDateTime eventDate;
    private Location location;
    private boolean paid;
    private int participantLimit;
    private JsonNullable<Boolean> requestModeration;
}
