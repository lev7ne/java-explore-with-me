package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
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
import ru.practicum.ewm.util.helper.ObjectCounter;
import ru.practicum.ewm.util.helper.ObjectFinder;
import ru.practicum.ewm.util.helper.ObjectMerger;
import ru.practicum.ewm.util.validator.EventDateValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPrivateServiceImpl implements EventPrivateService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    /**
     * Endpoint: POST "/users/{userId}/events"
     *
     * @param newEventDto
     * @param userId
     * @return returns an EventFullDto after creation
     */
    @Override
    @Transactional
    public EventFullDto create(NewEventDto newEventDto, Long userId) {
        EventDateValidator.isDateIsNotBefore(newEventDto.getEventDate(), 2);
        Event anyEvent = EventMapper.toEventFromNewEventDto(newEventDto);

        anyEvent.setInitiator(ObjectFinder.findUserById(userRepository, userId));
        anyEvent.setCategory(ObjectFinder.findCategoryById(categoryRepository, newEventDto.getCategory()));

        return EventMapper.toEventFullDtoFromEvent(eventRepository.save(anyEvent));
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
    public EventFullDto getById(Long userId, Long eventId) {
        Event event = ObjectFinder.findEventById(eventRepository, eventId);

        EventFullDto eventFullDto = EventMapper.toEventFullDtoFromEvent(event);

        eventFullDto.setViews(ObjectCounter.countViewsById(eventId, statsClient));
        eventFullDto.setConfirmedRequests(requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED));

        return eventFullDto;
    }

    /**
     * Endpoint: PATCH "/users/{userId}/events/{eventId}"
     *
     * @param updateEventUserRequest
     * @param userId
     * @param eventId
     * @return EventFullDto
     */
    @Override
    @Transactional
    public EventFullDto update(UpdateEventUserRequest updateEventUserRequest, Long userId, Long eventId) {
        Event updatedEvent = ObjectFinder.findEventById(eventRepository, eventId);

        if (updateEventUserRequest.getEventDate() != null) {
            EventDateValidator.isDateIsNotBefore(updateEventUserRequest.getEventDate(), 2);
        }

        if (updatedEvent.getState() == Event.State.PUBLISHED) {
            throw new ConditionMismatchException("Only pending or canceled events can be changed");
        }

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction() == UpdateEventUserRequest.StateAction.CANCEL_REVIEW) {
                updatedEvent.setState(Event.State.CANCELED);
            } else {
                updatedEvent.setState(Event.State.PENDING);
            }
        }

        ObjectMerger.copyProperties(updatedEvent, updateEventUserRequest);

        eventRepository.save(updatedEvent);

        EventFullDto eventFullDto = EventMapper.toEventFullDtoFromEvent(updatedEvent);

        eventFullDto.setViews(ObjectCounter.countViewsById(eventId, statsClient));
        eventFullDto.setConfirmedRequests(requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED));

        return eventFullDto;
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
                .map(EventMapper::toEventShortDtoFromEvent)
                .peek(eventShortDto -> {
                    eventShortDto.setViews(countViews.get(eventShortDto.getId()));
                    eventShortDto.setConfirmedRequests(confirmedRequests.get(eventShortDto.getId()));
                })
                .collect(Collectors.toList());
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
        // FIXME: метод выгружает лишнюю информацию из БД, а по двум параметрам найти не получается
//        List<Request> requests2 = requestRepository.getAllByEvent_Id(eventId)
//                .stream()
//                .filter(request -> request.getEvent().getInitiator().getId().equals(userId))
//                .collect(Collectors.toList());

        List<Request> requests = requestRepository.getByEventIdAndInitiatorId(eventId, userId);

        if (requests.isEmpty()) {
            return new ArrayList<>();
        }

        return requests.stream()
                .map(RequestMapper::toParticipationRequestDtoFromRequest)
                .collect(Collectors.toList());
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
        Event event = ObjectFinder.findEventById(eventRepository, eventId);

        List<Request> requestsToUpdate = requestRepository.getEventRequestsByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        for (Request request : requestsToUpdate) {
            if (request.getRequestStatus() != Request.RequestStatus.PENDING) {
                throw new ConditionMismatchException("Request must have status PENDING");
            }
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (eventRequestStatusUpdateRequest.getStatus().toString().equals(Request.RequestStatus.REJECTED.toString())) {
            rejectedRequests = requestsToUpdate.stream()
                    .map(request -> {
                        request.setRequestStatus(Request.RequestStatus.REJECTED);
                        requestRepository.save(request);
                        return RequestMapper.toParticipationRequestDtoFromRequest(request);
                    })
                    .collect(Collectors.toList());
        }

        Long count = requestRepository.countByEvent_IdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED);

        if (eventRequestStatusUpdateRequest.getStatus().toString().equals(Request.RequestStatus.CONFIRMED.toString())) {
            if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
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
