package ru.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.StatsClient;
import ru.ewm.event.dto.EventCreateDto;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventShortDto;
import ru.ewm.event.dto.EventUpdateUserDto;
import ru.ewm.event.mapper.EventMapper;
import ru.ewm.event.model.Event;
import ru.ewm.event.repository.EventRepository;
import ru.ewm.exception.ConditionMismatchException;
import ru.ewm.exception.NotFoundException;
import ru.ewm.request.dto.ParticipationRequestDto;
import ru.ewm.request.dto.RequestDto;
import ru.ewm.request.dto.RequestUpdateDto;
import ru.ewm.request.mapper.RequestMapper;
import ru.ewm.request.model.Request;
import ru.ewm.request.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatsClient statsClient;

    /**
     * Создание и добавление нового ивента в репозиторий (БД).
     *
     * @param createDto - DTO для создания нового ивента
     * @return EventDto - DTO возвращаемый пользователю
     */
    @Override
    @Transactional
    public EventDto create(EventCreateDto createDto, long initiatorId) {

        var event = eventMapper.map(createDto, initiatorId);
        event = eventRepository.save(event);

        return eventMapper.map(event);
    }

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public EventDto show(long eventId) {
        var event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));

        var dto = eventMapper.map(event);

//        dto.setViews(ObjectCounter.countViewsById(eventId, statsClient));
//        dto.setConfirmedRequests(requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED));

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
        var dto = eventMapper.map(event);

//        dto.setViews(ObjectCounter.countViewsById(id, statsClient));
//        dto.setConfirmedRequests(requestRepository.countByEvent_IdAndRequestStatus(id, Request.RequestStatus.CONFIRMED));

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
            return new ArrayList<>();
        }

//        List<Long> eventIds = events.stream()
//                .map(Event::getId)
//                .collect(Collectors.toList());

//        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);
//        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);

        return events.stream()
                .map(eventMapper::mapShort)
//                .peek(eventShortDto -> {
//                    eventShortDto.setViews(countViews.getOrDefault(eventShortDto.getId(), 0L));
//                    eventShortDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventShortDto.getId(), 0L));
//                })
                .toList();
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
                .map(requestMapper::map)
                .toList();

        return dtos;
    }

//    /**
//     *
//     */
//    @Override
//    @Transactional
//    public RequestDto update(RequestUpdateDto updateDto, long userId, long eventId) {
//        var event = eventRepository.findById(eventId).orElseThrow(() ->
//                new NotFoundException("Event with id=" + eventId + " was not found"));
//
//        List<Request> requests = requestRepository.findRequestByIdIn(updateDto.getRequestIds());
//
//        for (Request request : requests) {
//            if (request.getRequestStatus() != Request.RequestStatus.PENDING) {
//                throw new ConditionMismatchException("Request must have status PENDING");
//            }
//        }
//
//        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
//        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
//
//        if (updateDto.getStatus().toString().equals(Request.RequestStatus.REJECTED.toString())) {
//            rejectedRequests = requests.stream()
//                    .map(request -> {
//                        request.setRequestStatus(Request.RequestStatus.REJECTED);
//                        requestRepository.save(request);
//                        return requestMapper.map(request);
//                    })
//                    .collect(Collectors.toList());
//        }
//
//        var count = requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED);
//
//        if (updateDto.getStatus().toString().equals(Request.RequestStatus.CONFIRMED.toString())) {
//            if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
//                confirmedRequests = requests.stream()
//                        .map(request -> {
//                            request.setRequestStatus(Request.RequestStatus.CONFIRMED);
//                            requestRepository.save(request);
//                            return requestMapper.map(request);
//                        })
//                        .collect(Collectors.toList());
//            } else if (count >= event.getParticipantLimit()) {
//                throw new ConditionMismatchException("The participant limit has been reached");
//            }
//
//            for (Request request : requests) {
//                if (count < event.getParticipantLimit()) {
//                    request.setRequestStatus(Request.RequestStatus.CONFIRMED);
//                    confirmedRequests.add(requestMapper.map(request));
//                } else {
//                    request.setRequestStatus(Request.RequestStatus.REJECTED);
//                    rejectedRequests.add(requestMapper.map(request));
//                }
//                requestRepository.save(request);
//            }
//        }
//
//        return new RequestDto(confirmedRequests, rejectedRequests);
//    }

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

        // Process based on the new status
        if (newStatus == RequestUpdateDto.RequestStatus.REJECTED) {
            // Reject all requests
            for (Request request : requests) {
                request.setRequestStatus(Request.RequestStatus.REJECTED);
                requestRepository.save(request);
                rejectedRequests.add(requestMapper.map(request));
            }
        } else if (newStatus == RequestUpdateDto.RequestStatus.CONFIRMED) {
            // Check participant limit and moderation status
            if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
                // Confirm all requests if no limit or moderation
                for (Request request : requests) {
                    request.setRequestStatus(Request.RequestStatus.CONFIRMED);
                    requestRepository.save(request);
                    confirmedRequests.add(requestMapper.map(request));
                }
            } else {
                // Confirm requests within limit and reject the rest
                for (Request request : requests) {
                    if (confirmedCount < event.getParticipantLimit()) {
                        request.setRequestStatus(Request.RequestStatus.CONFIRMED);
                        confirmedRequests.add(requestMapper.map(request));
                        confirmedCount++;
                    } else {
                        request.setRequestStatus(Request.RequestStatus.REJECTED);
                        rejectedRequests.add(requestMapper.map(request));
                    }
                    requestRepository.save(request);
                }
            }
        }

        return new RequestDto(confirmedRequests, rejectedRequests);
    }
}
