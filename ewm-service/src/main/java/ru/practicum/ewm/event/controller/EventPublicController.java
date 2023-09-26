package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventPublicService;
import ru.practicum.ewm.util.exception.InvalidRequestException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/events")
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final EventPublicService eventPublicService;

    @GetMapping(value = "/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getById(@PathVariable Long eventId,
                                HttpServletRequest request) {

        return eventPublicService.getById(eventId, request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllWithParam(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                               @RequestParam(required = false)
                                               @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                               @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(required = false) Event.Sort sort,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                               @RequestParam(defaultValue = "10") @Positive Integer size,
                                               HttpServletRequest request) {

        Pageable pageable = PageRequest.of(from / size, size);

        if (sort != null) {
            if (sort == Event.Sort.EVENT_DATE) {
                pageable = PageRequest.of(from / size, size, Sort.by("eventDate"));
            } else if (sort == Event.Sort.VIEWS) {
                pageable = PageRequest.of(from / size, size, Sort.by("views"));
            } else {
                throw new InvalidRequestException("Incorrect sorting");
            }
        }

        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new InvalidRequestException("The dates of the range are specified incorrectly");
            }
        }

        return eventPublicService.getAllWithParam(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable, request);
    }
}
