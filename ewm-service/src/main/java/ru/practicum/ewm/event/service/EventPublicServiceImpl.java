package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.util.exception.NotFoundException;
import ru.practicum.ewm.util.helper.ObjectFinder;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService{
    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * EventPublicController method; endpoint: GET "events/{eventId}"
     *
     * @param id
     * @param request
     * @return EventFullDto
     */
    @Override
    @Transactional(readOnly = true)
    public EventFullDto getById(Long id, HttpServletRequest request) {
        Event event = ObjectFinder.findEventById(eventRepository, id);

        if (event.getState() != Event.State.PUBLISHED) {
            throw new NotFoundException("Event must be published");
        }

        statsClient.add(
                new EndpointHit(
                        "ewm-main-service",
                        request.getRequestURI(),
                        request.getRemoteAddr(),
                        LocalDateTime.now().format(formatter)
                )
        );

        event.setViews(countViews(request));
        eventRepository.save(event);

        return EventMapper.toEventFullDtoFromEvent(event);
    }

    /**
     * helper method for EventFullDto getById(Long id, HttpServletRequest request)
     *
     * @param request
     * @return number of views from the ewm-stats module
     */
    private long countViews(HttpServletRequest request) {
        List<ViewStats> stats = statsClient.getAll(
                LocalDateTime.now().minusYears(100).format(formatter),
                LocalDateTime.now().plusHours(1).format(formatter),
                List.of(request.getRequestURI()),
                true);

        if (stats.isEmpty()) {
            return 0L;
        }

        return stats.size();
    }

    /**
     * EventPublicController method; endpoint: GET "events/"
     *
     * @param text
     * @param categories
     * @param paid
     * @param rangeStart
     * @param rangeEnd
     * @param onlyAvailable
     * @param pageable
     * @param request
     * @return List<EventShortDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllWithParam(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Boolean onlyAvailable,
                                            Pageable pageable,
                                            HttpServletRequest request) {

        List<Event> events = eventRepository.getAllWithParam(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                pageable
        );

        if (onlyAvailable) {
            events = events.stream()
                    .filter(event -> event.getConfirmedRequests() < event.getParticipantLimit())
                    .collect(Collectors.toList());
        }

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        statsClient.add(
                new EndpointHit(
                        "ewm-main-service",
                        request.getRequestURI(),
                        request.getRemoteAddr(),
                        LocalDateTime.now().format(formatter)
                )
        );

        return events.stream()
                .map(EventMapper::toEventShortDtoFromEvent)
                .collect(Collectors.toList());
    }
}
