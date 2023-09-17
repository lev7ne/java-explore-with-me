package ru.practicum.ewm.event.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.Event;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/admin/events")
@Validated
public class EventAdminController {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @GetMapping
    public List<EventFullDto> get(@RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime start,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime end,
                                  @RequestParam(required = false) List<Long> userIds,
                                  @RequestParam(required = false) List<Event.EventState> eventStates,
                                  @RequestParam(required = false) List<Long> categoryIds,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                  @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return null;
    }

    @PatchMapping(value = "/{eventId}")
    public EventFullDto update(@Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                               @PathVariable Long eventId) {
        return null;
    }

}
