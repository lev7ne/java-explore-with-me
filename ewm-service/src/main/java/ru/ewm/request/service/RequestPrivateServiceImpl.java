package ru.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.event.model.Event;
import ru.ewm.event.repository.EventRepository;
import ru.ewm.exception.ConditionMismatchException;
import ru.ewm.exception.NotFoundException;
import ru.ewm.request.dto.ParticipationRequestDto;
import ru.ewm.request.mapper.RequestMapper;
import ru.ewm.request.model.Request;
import ru.ewm.request.repository.RequestRepository;
import ru.ewm.user.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RequestPrivateServiceImpl implements RequestPrivateService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    /**
     *
     */
    @Override
    @Transactional
    public ParticipationRequestDto create(long requesterId, long eventId) {

        // нельзя добавить повторный запрос (Ожидается код ошибки 409)
        if (requestRepository.findByRequesterIdAndEventId(requesterId, eventId).isPresent()) {
            throw new ConditionMismatchException("User's request for the event already exists");
        }

        var event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " was not found"));

        // инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
        if (requesterId == (event.getInitiator().getId())) {
            throw new ConditionMismatchException("User cannot create event request to his own event");
        }

        // нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        if (event.getState() != Event.State.PUBLISHED) {
            throw new ConditionMismatchException("The event must be published");
        }

        //если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        var count = requestRepository.countByEventIdAndRequestStatus(eventId, Request.RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0) {
            if (count >= event.getParticipantLimit()) {
                throw new ConditionMismatchException("The participant limit has been reached");
            }
        }

        var requester = userRepository.findById(requesterId).orElseThrow(() ->
                new NotFoundException("User with id=" + requesterId + " was not found"));

        var request = Request.builder()
                .requester(requester)
                .event(event)
                // если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
                .requestStatus(
                        event.getParticipantLimit() == 0 || !event.isRequestModeration() ?
                                Request.RequestStatus.CONFIRMED : Request.RequestStatus.PENDING)
                .build();

        request = requestRepository.save(request);
        var dto = requestMapper.map(request);

        return dto;
    }

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> index(long requesterId) {
        List<Request> requests = requestRepository.findAllByRequesterId(requesterId);

        if (requests.isEmpty()) {
            return List.of();
        }

        List<ParticipationRequestDto> dtos = requests.stream()
                .map(requestMapper::map)
                .toList();

        return dtos;
    }

    /**
     *
     */
    @Override
    @Transactional
    public ParticipationRequestDto cancel(long requesterId, long requestId) {
        var request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request with id=" + requestId + " was not found"));

        request.setRequestStatus(Request.RequestStatus.CANCELED);

        request = requestRepository.save(request);
        var dto = requestMapper.map(request);

        return dto;
    }
}
