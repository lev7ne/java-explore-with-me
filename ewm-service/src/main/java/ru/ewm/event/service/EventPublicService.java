package ru.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventParamDto;
import ru.ewm.event.dto.EventShortDto;

import java.util.List;


public interface EventPublicService {
    EventDto show(long eventId, HttpServletRequest request);

    List<EventShortDto> index(EventParamDto paramDto, Pageable pageable, HttpServletRequest request);
}
