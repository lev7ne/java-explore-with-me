package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.util.exception.InvalidRequestException;
import ru.practicum.ewm.util.exception.ValidationException;
import ru.practicum.ewm.util.helper.ObjectCounter;
import ru.practicum.ewm.util.helper.ObjectFinder;
import ru.practicum.ewm.util.helper.ObjectMerger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    /**
     * EventAdminController method; endpoint: PATCH "/admin/events/{eventId}"
     *
     * @param eventId
     * @param updateEventAdminRequest
     * @return EventFullDto
     */
    @Override
    @Transactional
    public EventFullDto update(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event updatedEvent = ObjectFinder.findEventById(eventRepository, eventId);
        LocalDateTime newEventDate = updateEventAdminRequest.getEventDate();

        if (newEventDate != null) {
            if (newEventDate.isBefore(LocalDateTime.now())) {
                throw new InvalidRequestException("Cannot create the event because event date cannot be earlier " +
                        "than current moment");
            }

            LocalDateTime publishDate = updatedEvent.getPublishedOn();

            if (newEventDate.isBefore(publishDate.minusHours(1))) {
                throw new InvalidRequestException("Cannot update the event date because it is earlier than the " +
                        "publication date more than an hour");
            }
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updatedEvent.getState() != Event.State.PENDING) {
                throw new ValidationException("Cannot publish or reject the event because it's not " +
                        "in the right state: " + updatedEvent.getState());
            }

            if (updateEventAdminRequest.getStateAction() == UpdateEventAdminRequest.StateAction.PUBLISH_EVENT) {
                updatedEvent.setState(Event.State.PUBLISHED);
                updatedEvent.setPublishedOn(LocalDateTime.now());
            } else {
                updatedEvent.setState(Event.State.CANCELED);
            }
        }

        if (updateEventAdminRequest.getLocation() != null) {
            updatedEvent.setLat(updatedEvent.getLat());
            updatedEvent.setLon(updatedEvent.getLon());
        }

        ObjectMerger.copyProperties(updateEventAdminRequest, updatedEvent);

        EventFullDto eventFullDto = EventMapper.toEventFullDtoFromEvent(eventRepository.save(updatedEvent));

        eventFullDto.setViews(ObjectCounter.countViewsById(eventId, statsClient));
        eventFullDto.setConfirmedRequests(requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED));

        return eventFullDto;
    }

    /**
     * Endpoint: GET "/admin/events"
     *
     * @param initiators
     * @param states
     * @param categories
     * @param rangeStart
     * @param rangeEnd
     * @param pageable
     * @return List<EventFullDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAll(
            List<Long> initiators,
            List<Event.State> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable
    ) {

        List<Event> events = eventRepository.getAllForAdminWithParam(
                initiators,
                states,
                categories,
                rangeStart,
                rangeEnd,
                pageable
        );

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

        return events.stream()
                .map(EventMapper::toEventFullDtoFromEvent)
                .peek(eventShortDto -> {
                    eventShortDto.setViews(countViews.getOrDefault(eventShortDto.getId(), 0L));
                    eventShortDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventShortDto.getId(), 0L));
                })
                .collect(Collectors.toList());

    }
}
