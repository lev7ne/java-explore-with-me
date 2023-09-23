package ru.practicum.ewm.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.entity.Request;

@UtilityClass
public class RequestMapper {
    public ParticipationRequestDto toParticipationRequestDtoFromRequest(Request request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreateDate())
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getRequestStatus())
                .build();
    }
}
