package ru.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventParamDto;
import ru.ewm.event.dto.EventShortDto;
import ru.ewm.event.mapper.EventMapper;
import ru.ewm.event.model.Event;
import ru.ewm.event.repository.EventRepository;
import ru.ewm.event.specification.EventSpecification;
import ru.ewm.stat.service.StatsService;
import ru.ewm.util.exception.NotFoundException;

import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventSpecification eventSpecification;
    private final StatsService statsService;

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public EventDto show(long id, HttpServletRequest request) {
        var event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Event with id=" + id + " was not found"));

        if (event.getState() != Event.State.PUBLISHED) {
            throw new NotFoundException("Event must be published");
        }

        statsService.addView(request);

        Map<Long, Long> requests = statsService.getConfirmedRequests(List.of(id));
        Map<Long, Long> views = statsService.getViews(List.of(id));

        var dto = eventMapper.toDto(event, views, requests);

        return dto;
    }

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> index(EventParamDto paramDto, Pageable pageable, HttpServletRequest request) {
        var spec = eventSpecification.build(paramDto);

        List<Event> events = eventRepository.findAll(spec, pageable).getContent();

        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();

        statsService.addView(request);

        Map<Long, Long> requests = statsService.getConfirmedRequests(ids);

        if (paramDto.isOnlyAvailable()) {
            events = events.stream()
                    .filter(event -> requests.get(event.getId()) < event.getParticipantLimit())
                    .toList();
        }

        if (events.isEmpty()) {
            return List.of();
        }

        Map<Long, Long> views = statsService.getViews(ids);

        List<EventShortDto> dtos = events.stream()
                .map(event -> eventMapper.toShortDto(event, views, requests))
                .toList();

        return dtos;
    }
}
