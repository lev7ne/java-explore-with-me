package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventParamDto;
import ru.practicum.ewm.event.dto.EventUpdateAdminDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.specification.PostSpecification;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.util.exception.InvalidRequestException;
import ru.practicum.ewm.util.exception.NotFoundException;
import ru.practicum.ewm.util.exception.ValidationException;
import ru.practicum.ewm.util.helper.ObjectCounter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final PostSpecification postSpecification;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    /**
     * EventAdminController method; endpoint: PATCH "/admin/events/{eventId}"
     *
     * @param eventId
     * @param eventUpdateAdminDto
     * @return EventFullDto
     */
    @Override
    @Transactional
    public EventDto update(long eventId, EventUpdateAdminDto eventUpdateAdminDto) {
        var event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));


        LocalDateTime newEventDate = eventUpdateAdminDto.getEventDate();

        if (newEventDate != null) {
            if (newEventDate.isBefore(LocalDateTime.now())) {
                throw new InvalidRequestException("Cannot create the event because event date cannot be earlier " +
                        "than current moment");
            }

            LocalDateTime publishDate = event.getPublishedOn();

            if (newEventDate.isBefore(publishDate.minusHours(1))) {
                throw new InvalidRequestException("Cannot update the event date because it is earlier than the " +
                        "publication date more than an hour");
            }
        }

        if (eventUpdateAdminDto.getStateAction() != null) {
            if (event.getState() != Event.State.PENDING) {
                throw new ValidationException("Cannot publish or reject the event because it's not " +
                        "in the right state: " + event.getState());
            }

            if (eventUpdateAdminDto.getStateAction() == EventUpdateAdminDto.StateAction.PUBLISH_EVENT) {
                event.setState(Event.State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                event.setState(Event.State.CANCELED);
            }
        }

        if (eventUpdateAdminDto.getLocation() != null) {
            event.setLat(event.getLat());
            event.setLon(event.getLon());
        }

        eventMapper.update(eventUpdateAdminDto, event);

        var dto = eventMapper.map(eventRepository.save(event));

        dto.setViews(ObjectCounter.countViewsById(eventId, statsClient));
        dto.setConfirmedRequests(requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED));

        return dto;
    }

    /**
     *
     * @param paramDto
     * @param pageable
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventDto> index(EventParamDto paramDto, Pageable pageable) {
        var spec = postSpecification.build(paramDto);
        var events = eventRepository.findAll(spec, pageable).getContent();

        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

        var dtos = events.stream()
                .map(eventMapper::map)
                .peek(eventShortDto -> {
                    eventShortDto.setViews(countViews.getOrDefault(eventShortDto.getId(), 0L));
                    eventShortDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventShortDto.getId(), 0L));
                })
                .toList();

        return dtos;
    }
}
