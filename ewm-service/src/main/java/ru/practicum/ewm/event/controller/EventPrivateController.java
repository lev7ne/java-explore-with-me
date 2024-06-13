package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventCreateDto;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUpdateUserDto;
import ru.practicum.ewm.event.service.EventPrivateService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(value = "/users/{userId}/events")
@Validated
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventPrivateService eventPrivateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventDto> create(@Valid @RequestBody EventCreateDto eventCreateDto,
                                           @PathVariable long userId) {

        var dto = eventPrivateService.create(eventCreateDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    @GetMapping(value = "/{eventId}")
    public ResponseEntity<EventDto> show(@PathVariable Long userId,
                         @PathVariable Long eventId) {
        var dto = eventPrivateService.show(userId, eventId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }

    @PatchMapping(value = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto update(@Valid @RequestBody EventUpdateUserDto eventUpdateUserDto,
                           @PathVariable Long userId,
                           @PathVariable Long eventId) {
        return eventPrivateService.update(eventUpdateUserDto, userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllByUserId(@PathVariable Long userId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventPrivateService.getAllByUserId(userId, pageable);
    }

    @GetMapping(value = "/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        return eventPrivateService.getEventRequests(userId, eventId);
    }

    @PatchMapping(value = "/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(@RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                              @PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return eventPrivateService.updateRequestStatus(eventRequestStatusUpdateRequest, userId, eventId);
    }
}
