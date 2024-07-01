package ru.ewm.user.dto;

import lombok.Getter;
import lombok.Setter;
import ru.ewm.event.dto.EventShortDto;

import java.util.List;


@Getter
@Setter
public class UserEventsDto {
    private long id;
    private String name;
    private String email;
    private List<EventShortDto> publishedSubEvents;
}
