package ru.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.event.dto.EventCreateDto;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventShortDto;
import ru.ewm.event.dto.EventUpdateUserDto;
import ru.ewm.event.mapper.EventMapper;
import ru.ewm.event.model.Event;
import ru.ewm.event.repository.EventRepository;
import ru.ewm.request.dto.ParticipationRequestDto;
import ru.ewm.request.dto.RequestDto;
import ru.ewm.request.dto.RequestUpdateDto;
import ru.ewm.request.mapper.RequestMapper;
import ru.ewm.request.model.Request;
import ru.ewm.request.repository.RequestRepository;
import ru.ewm.stat.service.StatsService;
import ru.ewm.util.exception.ConditionMismatchException;
import ru.ewm.util.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatsService statsService;

    /**
     * Создание и добавление нового ивента в репозиторий (БД).
     *
     * @param createDto - DTO для создания нового ивента
     * @return EventDto - DTO возвращаемый пользователю
     */
    @Override
    @Transactional
    public EventDto create(EventCreateDto createDto, long initiatorId) {

        var event = eventMapper.toEntity(createDto, initiatorId);
        event = eventRepository.save(event);

        return eventMapper.toDto(event);
    }

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public EventDto show(long id) {
        var event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Event with id=" + id + " was not found"));

        Map<Long, Long> requests = statsService.getConfirmedRequests(List.of(id));
        Map<Long, Long> views = statsService.getViews(List.of(id));

        var dto = eventMapper.toDto(event, requests, views);

        return dto;
    }

    /**
     *
     */
    @Override
    @Transactional
    public EventDto update(EventUpdateUserDto updateDto, long id) {
        var event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Event with id=" + id + " was not found"));

        if (event.getState() == Event.State.PUBLISHED) {
            throw new ConditionMismatchException("Only pending or canceled events can be changed");
        }

        if (updateDto.getStateAction() == EventUpdateUserDto.StateAction.CANCEL_REVIEW) {
            event.setState(Event.State.CANCELED);
        } else {
            event.setState(Event.State.PENDING);
        }

        eventMapper.update(updateDto, event);
        event = eventRepository.save(event);

        Map<Long, Long> requests = statsService.getConfirmedRequests(List.of(id));
        Map<Long, Long> views = statsService.getViews(List.of(id));

        var dto = eventMapper.toDto(event, requests, views);

        return dto;
    }

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> index(long userId, Pageable pageable) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);

        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Long> requests = statsService.getConfirmedRequests(ids);
        Map<Long, Long> views = statsService.getViews(ids);

        List<EventShortDto> dtos = events.stream()
                .map(event -> eventMapper.toShortDto(event, views, requests))
                .toList();

        return dtos;
    }

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> index(long userId, long eventId) {
        List<Request> requests = requestRepository.findByEventIdAndEventInitiatorId(eventId, userId);

        if (requests.isEmpty()) {
            return new ArrayList<>();
        }

        var dtos = requests.stream()
                .map(requestMapper::toDto)
                .toList();

        return dtos;
    }

    @Override
    @Transactional
    public RequestDto update(RequestUpdateDto updateDto, long userId, long eventId) {
        var event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));

        List<Request> requests = requestRepository.findRequestByIdIn(updateDto.getRequestIds());

        for (Request request : requests) {
            if (request.getRequestStatus() != Request.RequestStatus.PENDING) {
                throw new ConditionMismatchException("Request must have status PENDING");
            }
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        RequestUpdateDto.RequestStatus newStatus = updateDto.getStatus();
        long confirmedCount = requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED);

        if (newStatus == RequestUpdateDto.RequestStatus.REJECTED) {
            for (Request request : requests) {
                request.setRequestStatus(Request.RequestStatus.REJECTED);
                requestRepository.save(request);
                rejectedRequests.add(requestMapper.toDto(request));
            }
        } else if (newStatus == RequestUpdateDto.RequestStatus.CONFIRMED) {
            if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
                for (Request request : requests) {
                    request.setRequestStatus(Request.RequestStatus.CONFIRMED);
                    requestRepository.save(request);
                    confirmedRequests.add(requestMapper.toDto(request));
                }
            } else {
                for (Request request : requests) {
                    if (confirmedCount < event.getParticipantLimit()) {
                        request.setRequestStatus(Request.RequestStatus.CONFIRMED);
                        confirmedRequests.add(requestMapper.toDto(request));
                        confirmedCount++;
                    } else {
                        request.setRequestStatus(Request.RequestStatus.REJECTED);
                        rejectedRequests.add(requestMapper.toDto(request));
                    }
                    requestRepository.save(request);
                }
            }
        }

        return new RequestDto(confirmedRequests, rejectedRequests);
    }
}
