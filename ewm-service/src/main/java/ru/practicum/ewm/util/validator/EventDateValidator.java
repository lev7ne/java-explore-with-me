package ru.practicum.ewm.util.validator;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.util.exception.InvalidRequestException;

import java.time.LocalDateTime;

@UtilityClass
public class EventDateValidator {
    public static void isDateIsNotBefore(LocalDateTime eventDate, int hours) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(hours))) {
            throw new InvalidRequestException("Cannot create the event because event date cannot be earlier " +
                    "than " + hours + " hours after current moment");
        }
    }
}
