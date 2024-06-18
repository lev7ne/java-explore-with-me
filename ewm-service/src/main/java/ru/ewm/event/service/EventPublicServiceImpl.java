package ru.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.StatsClient;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventParamDto;
import ru.ewm.event.dto.EventShortDto;
import ru.ewm.event.mapper.EventMapper;
import ru.ewm.event.model.Event;
import ru.ewm.event.repository.EventRepository;
import ru.ewm.event.specification.EventSpecification;
import ru.ewm.exception.NotFoundException;
import ru.ewm.request.repository.RequestRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;
    private final EventSpecification eventSpecification;
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public EventDto show(long eventId, HttpServletRequest request) {
        var event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() != Event.State.PUBLISHED) {
            throw new NotFoundException("Event must be published");
        }

//        statsClient.add(
//                new EndpointHitCreateDto(
//                        "ewm-main-service",
//                        request.getRequestURI(),
//                        request.getRemoteAddr()
//                )
//        );

        var dto = eventMapper.map(event);
//        dto.setViews(countViews(request));

//        long count = requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED);
//        dto.setConfirmedRequests(count);

        return dto;
    }

//    /**
//     *
//     */
//    private long countViews(HttpServletRequest request) {
//        List<ViewStats> stats = statsClient.getAll(
//                LocalDateTime.now().minusYears(100).format(formatter),
//                LocalDateTime.now().plusHours(1).format(formatter),
//                List.of(request.getRequestURI()),
//                true);
//
//        if (stats.isEmpty()) {
//            return 0L;
//        }
//
//        return stats.size();
//    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> index(EventParamDto paramDto, Pageable pageable, HttpServletRequest request) {
        var spec = eventSpecification.build(paramDto);

        List<Event> events = eventRepository.findAll(spec, pageable).getContent();

        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

//        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
//        if (paramDto.isOnlyAvailable()) {
//            events = events.stream()
//                    .filter(event -> confirmedRequests.get(event.getId()) < event.getParticipantLimit())
//                    .toList();
//        }

//        statsClient.add(
//                new EndpointHitCreateDto(
//                        "ewm-main-service",
//                        request.getRequestURI(),
//                        request.getRemoteAddr()
//                )
//        );

//        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

        List<EventShortDto> dtos = events.stream()
                .map(eventMapper::mapShort)

//                .peek(eventShortDto -> {
//                    eventShortDto.setViews(countViews.get(eventShortDto.getId()));
//                    eventShortDto.setConfirmedRequests(confirmedRequests.get(eventShortDto.getId()));
//                })
                .toList();

        return dtos;
    }
}
