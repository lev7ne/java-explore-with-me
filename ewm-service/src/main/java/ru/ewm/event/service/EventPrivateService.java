package ru.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.ewm.event.dto.EventCreateDto;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventShortDto;
import ru.ewm.event.dto.EventUpdateUserDto;
import ru.ewm.request.dto.ParticipationRequestDto;
import ru.ewm.request.dto.RequestDto;
import ru.ewm.request.dto.RequestUpdateDto;

import java.util.List;


public interface EventPrivateService {
    EventDto create(EventCreateDto eventCreateDto, long userId);

    EventDto show(long eventId);

    EventDto update(EventUpdateUserDto eventUpdateUserDto, long eventId);

    List<EventShortDto> index(long userId, Pageable pageable);

    List<ParticipationRequestDto> index(long userId, long eventId);

    RequestDto updateStatusRequests(RequestUpdateDto requestUpdateDto, long eventId);
}
