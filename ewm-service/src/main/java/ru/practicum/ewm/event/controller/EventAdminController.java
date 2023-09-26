package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/admin/events")
@Validated
@RequiredArgsConstructor
public class EventAdminController {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final EventAdminService eventAdminService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAll(@RequestParam(required = false)
                                     @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                     @RequestParam(required = false)
                                     @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                     @RequestParam(required = false) List<Long> users,
                                     @RequestParam(required = false) List<Event.State> states,
                                     @RequestParam(required = false) List<Long> categories,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return eventAdminService.getAll(users, states, categories, rangeStart, rangeEnd, pageable);
    }

    @PatchMapping(value = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest,
                               @PathVariable Long eventId) {
        return eventAdminService.update(eventId, updateEventAdminRequest);
    }

}
