package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoWithEvents {
    private Long id;
    private String name;
    private String email;
    @Builder.Default
    private List<EventShortDto> publishedSubEvents = new ArrayList<>();
}
