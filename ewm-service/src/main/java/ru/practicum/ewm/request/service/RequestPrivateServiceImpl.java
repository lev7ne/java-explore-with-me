package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.entity.Request;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.exception.ConditionMismatchException;
import ru.practicum.ewm.util.helper.ObjectFinder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestPrivateServiceImpl implements RequestPrivateService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    /**
     * Endpoint: POST /users/{userId}/requests
     *
     * @param requesterId
     * @param eventId
     * @return ParticipationRequestDto
     */
    @Override
    @Transactional
    public ParticipationRequestDto create(Long requesterId, Long eventId) {
        if (!requestRepository.getAllByRequester_IdAndEvent_Id(requesterId, eventId).isEmpty()) {
            throw new ConditionMismatchException("User's request for the event already exists");
        }

        Event event = ObjectFinder.findEventById(eventRepository, eventId);

        if (event.getState() != Event.State.PUBLISHED) {
            throw new ConditionMismatchException("The event must be published");
        }

        if (event.getParticipantLimit() != 0) {
            if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                throw new ConditionMismatchException("The participant limit has been reached");
            }
        }

        if (requesterId.equals(event.getInitiator().getId())) {
            throw new ConditionMismatchException("User cannot create event request to his own event");
        }

        User requester = ObjectFinder.findUserById(userRepository, requesterId);

        Request request = Request.builder()
                .createDate(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .requestStatus(Request.RequestStatus.PENDING)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setRequestStatus(Request.RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        return RequestMapper.toParticipationRequestDtoFromRequest(requestRepository.save(request));
    }

    /**
     * Endpoint: GET /users/{userId}/requests
     *
     * @param requesterId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAll(Long requesterId) {
        ObjectFinder.findUserById(userRepository, requesterId);

        List<Request> requests = requestRepository.getAllByRequester_Id(requesterId);

        if (requests.isEmpty()) {
            return new ArrayList<>();
        }

        return requests.stream()
                .map(RequestMapper::toParticipationRequestDtoFromRequest)
                .collect(Collectors.toList());
    }

    /**
     * Endpoint: PATCH /users/{userId}/requests/{requestId}/cancel
     *
     * @param requesterId
     * @param requestId
     * @return ParticipationRequestDto
     */
    @Override
    @Transactional
    public ParticipationRequestDto cancel(Long requesterId, Long requestId) {
        log.info(requesterId + " / " + requestId);
        Request canceledRequest = ObjectFinder.findRequestById(requestRepository, requestId);
        log.info("Найденный реквест" + canceledRequest);
        canceledRequest.setRequestStatus(Request.RequestStatus.CANCELED);

        return RequestMapper.toParticipationRequestDtoFromRequest(requestRepository.save(canceledRequest));
    }
}
