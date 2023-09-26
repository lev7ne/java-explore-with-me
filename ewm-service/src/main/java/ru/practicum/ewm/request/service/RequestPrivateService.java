package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestPrivateService {
    ParticipationRequestDto create(Long requesterId, Long eventId);

    ParticipationRequestDto cancel(Long requesterId, Long requestId);

    List<ParticipationRequestDto> getAll(Long requesterId);
}
