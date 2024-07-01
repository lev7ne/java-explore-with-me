package ru.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ewm.request.dto.ParticipationRequestDto;
import ru.ewm.request.service.RequestPrivateService;
import ru.ewm.util.exception.InvalidRequestException;

import java.util.List;


@RestController
@RequestMapping(value = "/users/{requesterId}/requests")
@RequiredArgsConstructor
public class RequestPrivateController {
    private final RequestPrivateService requestPrivateService;

    @GetMapping("")
    public ResponseEntity<List<ParticipationRequestDto>> index(@PathVariable long requesterId) {
        List<ParticipationRequestDto> dtos = requestPrivateService.index(requesterId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    @PostMapping("")
    public ResponseEntity<ParticipationRequestDto> create(@PathVariable long requesterId,
                                                          @RequestParam(required = false) Long eventId) {

        if (eventId == null) {
            throw new InvalidRequestException(
                    "Required request parameter 'eventId' for method parameter type Long is not present");
        }

        var dto = requestPrivateService.create(requesterId, eventId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    @PatchMapping(value = "/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancel(@PathVariable long requesterId,
                                                          @PathVariable long requestId) {

        var dto = requestPrivateService.cancel(requesterId, requestId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }
}
