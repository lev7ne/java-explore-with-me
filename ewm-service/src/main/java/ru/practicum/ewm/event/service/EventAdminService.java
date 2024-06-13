package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventParamDto;
import ru.practicum.ewm.event.dto.EventUpdateAdminDto;

import java.util.List;

public interface EventAdminService {
    EventDto update(long eventId, EventUpdateAdminDto eventUpdateAdminDto);

    List<EventDto> index(EventParamDto paramDto, Pageable pageable);
}
