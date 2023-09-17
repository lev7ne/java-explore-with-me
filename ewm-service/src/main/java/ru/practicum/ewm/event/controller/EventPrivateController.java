package ru.practicum.ewm.event.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(value = "/users/{userId}/events")
@Validated
public class EventPrivateController {
    @GetMapping
    public List<EventShortDto> getByUserId(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        return null;
    }

    @PostMapping
    public EventFullDto create(@Valid @RequestBody NewEventDto newEventDto,
                               @PathVariable Long userId) {
        return null;
    }

    @GetMapping(value = "/{eventId}")
    public EventFullDto getByUserIdAndEventId(@PathVariable Long userId,
                                              @PathVariable Long eventId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = "10") @Positive Integer size) {
        return null;
    }

    @PatchMapping(value = "/{eventId}")
    public EventFullDto update(@Valid @RequestBody UpdateEventUserRequest updateEventAdminRequest,
                               @PathVariable Long userId,
                               @PathVariable Long eventId) {
        return null;
    }

    @GetMapping(value = "/{eventId}/requests") // ParticipationRequestDto
    public List<UpdateEventAdminRequest> getRequestsByEventId(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return null;
    }

    @PatchMapping(value = "/{eventId}/requests") // EventRequestStatusUpdateRequest
    public UpdateEventAdminRequest updateRequests(@Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                  @PathVariable Long userId,
                                                  @PathVariable Long eventId) {
        return null;
    }
}
