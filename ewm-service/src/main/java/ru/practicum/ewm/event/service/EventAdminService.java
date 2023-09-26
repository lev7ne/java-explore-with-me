package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminService {
    EventFullDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> getAll(
            List<Long> initiators,
            List<Event.State> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable
    );
}
