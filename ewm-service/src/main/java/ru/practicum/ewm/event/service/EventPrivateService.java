package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventPrivateService {
    EventFullDto create(NewEventDto newEventDto, Long userId);

    EventFullDto getById(Long userId, Long eventId);

    EventFullDto update(UpdateEventUserRequest updateEventUserRequest, Long userId, Long eventId);

    List<EventShortDto> getAllByUserId(Long userId, Pageable pageable);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            Long userId,
            Long eventId
    );
}
