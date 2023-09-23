package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestPrivateService;
import ru.practicum.ewm.util.exception.InvalidRequestException;

import java.util.List;

@RestController
@RequestMapping(value = "/users/{requesterId}/requests")
@RequiredArgsConstructor
@Validated
public class RequestPrivateController {
    private final RequestPrivateService requestPrivateService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAll(@PathVariable Long requesterId) {
        return requestPrivateService.getAll(requesterId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable Long requesterId,
                                          @RequestParam(required = false) Long eventId) {
        if (eventId == null) {
            throw new InvalidRequestException("Required request parameter 'eventId' for " +
                    "method parameter type Long is not present");
        }
        return requestPrivateService.create(requesterId, eventId);
    }

    @PatchMapping(value = "/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancel(@PathVariable Long requesterId,
                                          @PathVariable Long requestId) {
        return requestPrivateService.cancel(requesterId, requestId);
    }
}
