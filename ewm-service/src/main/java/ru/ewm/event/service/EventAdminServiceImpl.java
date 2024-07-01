package ru.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventParamDto;
import ru.ewm.event.dto.EventUpdateAdminDto;
import ru.ewm.event.mapper.EventContext;
import ru.ewm.event.mapper.EventMapper;
import ru.ewm.event.model.Event;
import ru.ewm.event.repository.EventRepository;
import ru.ewm.event.specification.EventSpecification;
import ru.ewm.stat.service.StatsService;
import ru.ewm.util.exception.NotFoundException;
import ru.ewm.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {
    private final EventRepository eventRepository;
    private final EventSpecification eventSpecification;
    private final EventMapper eventMapper;
    private final StatsService statsService;

    /**
     *
     */
    @Override
    @Transactional
    public EventDto update(long id, EventUpdateAdminDto updateDto) {
        var event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Event with id=" + id + " was not found"));

        var stateAction = event.getState();
        var action = updateDto.getStateAction();

        if (stateAction != null && action != null) {
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
                        default:
                    }
                default:
            }
        }

        eventMapper.update(updateDto, event);
        event = eventRepository.save(event);

        EventContext context = statsService.createEventContext(List.of(id));
        var dto = eventMapper.toDto(event, context);

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

        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();

        EventContext context = statsService.createEventContext(ids);
        List<EventDto> dtos = events.stream()
                .map(event -> eventMapper.toDto(event, context))
                .toList();

        return dtos;
    }
}
