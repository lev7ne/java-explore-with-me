package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventCreateDto;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUpdateUserDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.exception.ConditionMismatchException;
import ru.practicum.ewm.util.exception.NotFoundException;
import ru.practicum.ewm.util.helper.ObjectCounter;
import ru.practicum.ewm.util.validator.EventDateValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    // FIXME: поправить javadoc после тестов
    /**
     * Endpoint: POST "/users/{userId}/events"
     *
     * @param eventCreateDto
     * @param creatorId
     * @return returns an EventFullDto after creation
     */
    @Override
    @Transactional
    public EventDto create(EventCreateDto eventCreateDto, long creatorId) {
        // FIXME: создать кастомную аннотацию
        EventDateValidator.isDateIsNotBefore(eventCreateDto.getEventDate(), 2);

        var event = eventMapper.map(eventCreateDto, creatorId);
        event = eventRepository.save(event);

        return eventMapper.map(event);
    }

    /**
     * Endpoint: GET "/users/{userId}/events/{eventId}"
     *
     * @param userId
     * @param eventId
     * @return EventFullDto
     */
    @Override
    @Transactional(readOnly = true)
    public EventDto show(Long userId, Long eventId) {
        var event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));

        var dto = eventMapper.map(event);

        dto.setViews(ObjectCounter.countViewsById(eventId, statsClient));
        dto.setConfirmedRequests(requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED));

        return dto;
    }

    /**
     * Endpoint: PATCH "/users/{userId}/events/{eventId}"
     *
     * @param eventUpdateUserDto
     * @param userId
     * @param eventId
     * @return EventFullDto
     */
    @Override
    @Transactional
    public EventDto update(EventUpdateUserDto eventUpdateUserDto, Long userId, Long eventId) {
        var event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));

        if (eventUpdateUserDto.getEventDate() != null) {
            EventDateValidator.isDateIsNotBefore(eventUpdateUserDto.getEventDate(), 2);
        }

        if (event.getState() == Event.State.PUBLISHED) {
            throw new ConditionMismatchException("Only pending or canceled events can be changed");
        }

        if (eventUpdateUserDto.getStateAction() != null) {
            if (eventUpdateUserDto.getStateAction() == EventUpdateUserDto.StateAction.CANCEL_REVIEW) {
                event.setState(Event.State.CANCELED);
            } else {
                event.setState(Event.State.PENDING);
            }
        }

        eventMapper.update(eventUpdateUserDto, event);
        event = eventRepository.save(event);
        var dto = eventMapper.map(event);

        dto.setViews(ObjectCounter.countViewsById(eventId, statsClient));
        dto.setConfirmedRequests(requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED));

        return dto;
    }

    /**
     * Endpoint: GET "/users/{userId}/events/{eventId}"
     *
     * @param userId
     * @param pageable
     * @return List<EventShortDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByUserId(Long userId, Pageable pageable) {
        List<Event> events = eventRepository.getEventsByInitiator_Id(userId, pageable);

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);
        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);

        return events.stream()
                .map(eventMapper::mapShort)
                .peek(eventShortDto -> {
                    eventShortDto.setViews(countViews.getOrDefault(eventShortDto.getId(), 0L));
                    eventShortDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventShortDto.getId(), 0L));
                })
                .toList();
    }


    /**
     * Endpoint: GET "/users/{userId}/events/{eventId}/requests"
     *
     * @param userId
     * @param eventId
     * @return List<ParticipationRequestDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        List<Request> requests = requestRepository.getByEventIdAndInitiatorId(eventId, userId);

        if (requests.isEmpty()) {
            return List.of();
        }

        var dtos = requests.stream()
                .map(RequestMapper::toParticipationRequestDtoFromRequest)
                .toList();

        return dtos;
    }

    /**
     * Endpoint: PATCH "/users/{userId}/events/{eventId}/requests"
     *
     * @param eventRequestStatusUpdateRequest
     * @param userId
     * @param eventId
     * @return EventRequestStatusUpdateResult
     */
    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, Long userId, Long eventId) {
        var event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));

        List<Request> requestsToUpdate = requestRepository.getEventRequestsByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        for (Request request : requestsToUpdate) {
            if (request.getRequestStatus() != Request.RequestStatus.PENDING) {
                throw new ConditionMismatchException("Request must have status PENDING");
            }
        }

        List<ParticipationRequestDto> confirmedRequests = List.of();
        List<ParticipationRequestDto> rejectedRequests = List.of();

        if (eventRequestStatusUpdateRequest.getStatus().toString().equals(Request.RequestStatus.REJECTED.toString())) {
            rejectedRequests = requestsToUpdate.stream()
                    .map(request -> {
                        request.setRequestStatus(Request.RequestStatus.REJECTED);
                        requestRepository.save(request);
                        return RequestMapper.toParticipationRequestDtoFromRequest(request);
                    })
                    .collect(Collectors.toList());
        }

        long count = requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED);

        if (eventRequestStatusUpdateRequest.getStatus().toString().equals(Request.RequestStatus.CONFIRMED.toString())) {
            if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
                confirmedRequests = requestsToUpdate.stream()
                        .map(request -> {
                            request.setRequestStatus(Request.RequestStatus.CONFIRMED);
                            requestRepository.save(request);
                            return RequestMapper.toParticipationRequestDtoFromRequest(request);
                        })
                        .collect(Collectors.toList());
            } else if (count >= event.getParticipantLimit()) {
                throw new ConditionMismatchException("The participant limit has been reached");
            }

            for (Request request : requestsToUpdate) {
                if (count < event.getParticipantLimit()) {
                    request.setRequestStatus(Request.RequestStatus.CONFIRMED);
                    confirmedRequests.add(RequestMapper.toParticipationRequestDtoFromRequest(request));
                } else {
                    request.setRequestStatus(Request.RequestStatus.REJECTED);
                    rejectedRequests.add(RequestMapper.toParticipationRequestDtoFromRequest(request));
                }
                requestRepository.save(request);
            }
        }

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}
