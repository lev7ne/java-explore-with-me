package ru.ewm.request.service;

import ru.ewm.request.dto.ParticipationRequestDto;

import java.util.List;


public interface RequestPrivateService {
    ParticipationRequestDto create(long requesterId, long eventId);

    ParticipationRequestDto cancel(long requesterId, long requestId);

    List<ParticipationRequestDto> index(long requesterId);
}
