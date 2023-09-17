package ru.practicum.ewm.compilation.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private List<EventShortDto> events;
}
