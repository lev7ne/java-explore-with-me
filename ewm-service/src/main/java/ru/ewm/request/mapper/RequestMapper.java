package ru.ewm.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.ewm.request.dto.ParticipationRequestDto;
import ru.ewm.request.model.Request;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class RequestMapper {
    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "created", source = "createDate")
    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "status", source = "requestStatus")
    public abstract ParticipationRequestDto map(Request model);
}
