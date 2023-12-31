package ru.practicum.ewm.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.Location;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.mapper.UserMapper;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public Event toEventFromNewEventDto(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .title(newEventDto.getTitle())
                .publishedOn(LocalDateTime.now().plusHours(2))
                .createDate(LocalDateTime.now())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .paid(newEventDto.getPaid())
                .state(Event.State.PENDING)
                .build();
    }

    public EventFullDto toEventFullDtoFromEvent(Event event) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper
                        .toCategoryDtoFromCategory(event.getCategory()))
                .createdOn(event.getCreateDate())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper
                        .toUserShortDtoFromUser(event.getInitiator()))
                .location(Location
                        .builder()
                        .lat(event.getLat())
                        .lon(event.getLon())
                        .build())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public EventShortDto toEventShortDtoFromEvent(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDtoFromCategory(event.getCategory()))
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDtoFromUser(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }
}
