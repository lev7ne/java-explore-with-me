package ru.ewm.event.mapper;

import org.mapstruct.*;
import ru.ewm.category.mapper.CategoryMapper;
import ru.ewm.event.dto.*;
import ru.ewm.event.model.Event;
import ru.ewm.user.mapper.UserMapper;
import ru.ewm.util.mapper.JsonNullableMapper;


@Mapper(
        uses =
                {
                        JsonNullableMapper.class,
                        UserMapper.class,
                        CategoryMapper.class
                },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class EventMapper {
    @Mapping(target = "category.id", source = "dto.category")
    @Mapping(target = "initiator.id", source = "initiatorId")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "lat", source = "dto.location.lat")
    @Mapping(target = "lon", source = "dto.location.lon")
    @Mapping(target = "paid", defaultValue = "false")
    @Mapping(target = "participantLimit", defaultValue = "0")
    @Mapping(target = "requestModeration", source = "dto.requestModeration", defaultValue = "true")
    public abstract Event toEntity(EventCreateDto dto, Long initiatorId);

    //FIXME: убрать этот маппер после тестов!
    @Mapping(target = "category", source = "model.category")
    @Mapping(target = "initiator", source = "model.initiator")
    @Mapping(target = "location.lat", source = "model.lat")
    @Mapping(target = "location.lon", source = "model.lon")
    @Mapping(target = "createdOn", source = "model.createdDate")
    public abstract EventDto toDto(Event model);

    @Mapping(target = "category", source = "model.category")
    @Mapping(target = "initiator", source = "model.initiator")
    @Mapping(target = "location.lat", source = "model.lat")
    @Mapping(target = "location.lon", source = "model.lon")
    @Mapping(target = "createdOn", source = "model.createdDate")
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    public abstract EventDto toDto(Event model, @Context EventContext context);

    @AfterMapping
    public void mapViewsAndConfirmedRequests(@MappingTarget EventDto dto,
                                             @Context EventContext context) {
        dto.setConfirmedRequests(context.getConfirmedRequests().getOrDefault(dto.getId(), 0L));
        dto.setViews(context.getViews().getOrDefault(dto.getId(), 0L));
    }

    //FIXME: убрать этот маппер после тестов!
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    public abstract EventShortDto toShortDto(Event model);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    public abstract EventShortDto toShortDto(Event model, @Context EventContext context);

    @AfterMapping
    public void mapViewsAndConfirmedRequests(@MappingTarget EventShortDto dto,
                                             @Context EventContext context) {
        dto.setConfirmedRequests(context.getConfirmedRequests().getOrDefault(dto.getId(), 0L));
        dto.setViews(context.getViews().getOrDefault(dto.getId(), 0L));
    }

    @Mapping(target = "category.id", source = "dto.category")
    public abstract void update(EventUpdateUserDto dto, @MappingTarget Event model);

    @AfterMapping
    public void mapLocation(EventUpdateUserDto dto, @MappingTarget Event model) {
        if (dto.getLocation() != null && dto.getLocation().isPresent()) {
            Location location = dto.getLocation().get();
            model.setLat(location.getLat());
            model.setLon(location.getLon());
        }
    }

    @Mapping(target = "category.id", source = "dto.category")
    public abstract void update(EventUpdateAdminDto dto, @MappingTarget Event model);

    @AfterMapping
    public void mapLocation(EventUpdateAdminDto dto, @MappingTarget Event model) {
        if (dto.getLocation() != null && dto.getLocation().isPresent()) {
            Location location = dto.getLocation().get();
            model.setLat(location.getLat());
            model.setLon(location.getLon());
        }
    }
}
