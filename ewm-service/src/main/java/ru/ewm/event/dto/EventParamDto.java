package ru.ewm.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Builder
public class EventParamDto {
    private String text;
    private List<Long> users;
    private List<Long> categories;
    private boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private List<Event.State> states;
    private boolean onlyAvailable;
}
