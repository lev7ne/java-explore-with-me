package ru.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventParamDto;
import ru.ewm.event.dto.EventUpdateAdminDto;

import java.util.List;


public interface EventAdminService {
    EventDto update(long eventId, EventUpdateAdminDto updateAdminDto);

    List<EventDto> index(EventParamDto paramDto, Pageable pageable);
}
