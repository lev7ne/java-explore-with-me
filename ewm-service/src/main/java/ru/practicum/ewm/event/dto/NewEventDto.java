package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @NotBlank(message = "The event annotation cannot be empty")
    @Length(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @NotBlank(message = "The event description cannot be empty")
    @Length(min = 20, max = 7000)
    private String description;
    @Future
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid = false;
    private Long participantLimit = 0L;
    private Boolean requestModeration = true;
    @NotBlank(message = "The event title cannot be empty")
    @Length(min = 3, max = 120)
    private String title;
}
