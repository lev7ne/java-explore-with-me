package ru.ewm.event.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;
import ru.ewm.util.annotation.MainServiceAnnotation.NoEarlierThan2HoursBefore;

import java.time.LocalDateTime;


@Getter
@Setter
public class EventUpdateUserDto {
    @Size(min = 3, max = 120)
    private JsonNullable<String> title;
    @Size(min = 20, max = 2000)
    private JsonNullable<String> annotation;
    private JsonNullable<Long> category;
    @Size(min = 20, max = 7000)
    private JsonNullable<String> description;
    @Future
    @NoEarlierThan2HoursBefore
    private JsonNullable<LocalDateTime> eventDate;
    private JsonNullable<Location> location;
    private JsonNullable<Boolean> paid;
    private JsonNullable<Integer> participantLimit;
    private JsonNullable<Boolean> requestModeration;
    private StateAction stateAction;

    public enum StateAction {
        SEND_TO_REVIEW, CANCEL_REVIEW
    }
}
