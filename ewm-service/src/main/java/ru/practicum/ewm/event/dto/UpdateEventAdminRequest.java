package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@Builder
public class UpdateEventAdminRequest {
    @Length(min = 20, max = 2000)
    private String annotation;
    private Long category;
    @Length(min = 20, max = 7000)
    private String description;
    private LocalDateTime eventDate; // yyyy-MM-dd HH:mm:ss
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    @Length(min = 3, max = 120)
    private String title;

    public enum StateAction {
        PUBLISH_EVENT, REJECT_EVENT
    }
}
