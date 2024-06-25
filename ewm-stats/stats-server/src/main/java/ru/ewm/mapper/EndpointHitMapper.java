package ru.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.ewm.dto.EndpointHitCreateDto;
import ru.ewm.dto.EndpointHitDto;
import ru.ewm.model.EndpointHit;


@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class EndpointHitMapper {
    @Mapping(target = "timestamp", ignore = true)
    public abstract EndpointHit toEntity(EndpointHitCreateDto dto);

    @Mapping(target = "model.id", ignore = true)
    @Mapping(target = "timestamp", source = "model.timestamp")
    public abstract EndpointHitDto toDto(EndpointHit model);
}
