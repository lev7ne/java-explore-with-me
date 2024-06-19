package ru.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.ewm.dto.EndpointHitCreateDto;
import ru.ewm.dto.EndpointHitDto;
import ru.ewm.model.EndpointHit;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class EndpointHitMapper {

    public abstract EndpointHit toEntity(EndpointHitCreateDto dto);

    public abstract EndpointHitDto toDto(EndpointHit model);
}
