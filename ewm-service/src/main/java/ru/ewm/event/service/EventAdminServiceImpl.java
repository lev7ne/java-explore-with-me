package ru.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventParamDto;
import ru.ewm.event.dto.EventUpdateAdminDto;
import ru.ewm.event.mapper.EventMapper;
import ru.ewm.event.model.Event;
import ru.ewm.event.repository.EventRepository;
import ru.ewm.event.specification.EventSpecification;
import ru.ewm.request.repository.RequestRepository;
import ru.ewm.util.exception.NotFoundException;
import ru.ewm.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventSpecification eventSpecification;
    private final EventMapper eventMapper;

    /**
     *
     */
    @Override
    @Transactional
    public EventDto update(long eventId, EventUpdateAdminDto updateDto) {
        var event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));

        var stateAction = event.getState();
        var action = updateDto.getStateAction();

        switch (stateAction) {
            case CANCELED:
            case PUBLISHED:
                throw new ValidationException("Cannot publish or reject the event because it's not " +
                        "in the right state: " + event.getState());
            case PENDING:
                switch (action) {
                    case PUBLISH_EVENT:
                        event.setState(Event.State.PUBLISHED);
                        event.setPublishedOn(LocalDateTime.now());
                        break;
                    case REJECT_EVENT:
                        event.setState(Event.State.CANCELED);
                        break;
                }
        }

        eventMapper.update(updateDto, event);
        var dto = eventMapper.map(eventRepository.save(event));

//        dto.setViews(ObjectCounter.countViewsById(eventId, statsClient));
//        dto.setConfirmedRequests(requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED));

        return dto;
    }

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventDto> index(EventParamDto paramDto, Pageable pageable) {
        var spec = eventSpecification.build(paramDto);
        List<Event> events = eventRepository.findAll(spec, pageable).getContent();

        if (events.isEmpty()) {
            return List.of();
        }

//        List<Long> eventIds = events.stream()
//                .map(Event::getId)
//                .toList();

//        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
//        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

        var dtos = events.stream()
                .map(eventMapper::map)
//      TODO: добавить информацию о подтвержденных запросах и просмотрах

//                .peek(eventShortDto -> {
//                    eventShortDto.setViews(countViews.getOrDefault(eventShortDto.getId(), 0L));
//                    eventShortDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventShortDto.getId(), 0L));
//                })
                .toList();

        return dtos;
    }
}
