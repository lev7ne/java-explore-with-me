package ru.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.event.dto.EventCreateDto;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventShortDto;
import ru.ewm.event.dto.EventUpdateUserDto;
import ru.ewm.event.mapper.EventContext;
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


@Slf4j
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

        EventContext context = statsService.createEventContext(List.of(id));
        var dto = eventMapper.toDto(event, context);

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

        EventContext context = statsService.createEventContext(List.of(id));
        var dto = eventMapper.toDto(event, context);

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

        EventContext context = statsService.createEventContext(ids);
        List<EventShortDto> dtos = events.stream()
                .map(event -> eventMapper.toShortDto(event, context))
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

    /**
     *
     */
    @Override
    @Transactional
    public RequestDto updateStatusRequests(RequestUpdateDto updateDto, long id) {
        log.info("Поиск события по id={}", id);
        var event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Event with id=" + id + " was not found"));

        // Поиск запросов по ids
        List<Request> requests = requestRepository.findRequestByIdIn(updateDto.getRequestIds());

        // Проверка статуса каждого запроса
        for (Request request : requests) {
            if (request.getRequestStatus() != Request.RequestStatus.PENDING) {
                throw new ConditionMismatchException("Request must have status PENDING");
            }
        }

        // Подготовка списков для подтвержденных и отклоненных запросов
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        RequestUpdateDto.RequestStatus newStatus = updateDto.getStatus();
        long confirmedCount = requestRepository.countByEventIdAndRequestStatus(id, Request.RequestStatus.CONFIRMED);

        if (newStatus == RequestUpdateDto.RequestStatus.REJECTED) {
            // Обработка отклонения запросов
            for (Request request : requests) {
                request.setRequestStatus(Request.RequestStatus.REJECTED);
                rejectedRequests.add(requestMapper.toDto(request));
            }
        } else if (newStatus == RequestUpdateDto.RequestStatus.CONFIRMED) {
            // Обработка подтверждения запросов
            for (Request request : requests) {
                if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
                    // Подтверждение без учета лимита и модерации
                    request.setRequestStatus(Request.RequestStatus.CONFIRMED);
                    confirmedRequests.add(requestMapper.toDto(request));
                } else {
                    // Подтверждение с учетом лимита
                    if (confirmedCount < event.getParticipantLimit()) {
                        request.setRequestStatus(Request.RequestStatus.CONFIRMED);
                        confirmedRequests.add(requestMapper.toDto(request));
                        confirmedCount++;
                    } else {
                        throw new ConditionMismatchException("The participant limit has been reached");
                    }
                }
            }
        }

        // Сохранение всех изменений в репозитории
        requestRepository.saveAll(requests);

        return new RequestDto(confirmedRequests, rejectedRequests);
    }
}
