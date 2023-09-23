package ru.practicum.ewm.util.validator;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.util.exception.InvalidRequestException;

import java.time.LocalDateTime;

@UtilityClass
public class EventDateValidator {
    public static void eventDateValidation(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidRequestException("Cannot create the event because event date cannot be earlier " +
                    "than two hours after current moment");
        }
    }
}
