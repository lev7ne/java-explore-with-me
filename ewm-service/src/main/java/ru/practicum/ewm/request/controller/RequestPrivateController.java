package ru.practicum.ewm.request.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping(value = "/users/{userId}/requests")
public class RequestPrivateController {
    @GetMapping
    public List<ParticipationRequestDto> getByUserId(@PathVariable Long userId) {
        return null;
    }

    @PostMapping
    public ParticipationRequestDto create(@PathVariable Long userId,
                                          @RequestParam Long eventId) {
        return null;
    }

    @PatchMapping(value = "/{requestId}/cancel")
    public ParticipationRequestDto cancel(@PathVariable Long userId,
                                          @PathVariable Long requestId) {
        return null;
    }
}
