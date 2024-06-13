package ru.practicum.ewm.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@Builder
public class UserEventsDto {
    private Long id;
    private String name;
    private String email;
    @Builder.Default
    private List<EventShortDto> publishedSubEvents = List.of();
}
