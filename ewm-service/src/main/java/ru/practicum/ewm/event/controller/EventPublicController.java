package ru.practicum.ewm.event.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.util.helper.EventType;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping(value = "/events")
@Validated
public class EventPublicController {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final StatsClient statsClient;

    @Autowired
    public EventPublicController(StatsClient statsClient) {
        this.statsClient = statsClient;
    }

    @GetMapping
    public List<EventShortDto> get(@RequestParam(required = false) String text,
                                   @RequestParam(required = false) List<Long> categoryIds,
                                   @RequestParam(required = false) Boolean paid,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                   @RequestParam(required = false) Boolean onlyAvailable,
                                   @RequestParam(required = false) EventType sort,
                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                   @RequestParam(defaultValue = "10") @Positive Integer size,
                                   HttpServletRequest request) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        String formattedTimestamp = LocalDateTime.now().format(formatter);

        statsClient.add(EndpointHit
                .builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(formattedTimestamp)
                .build());

        return null;
    }

    @GetMapping(value = "/{eventId}")
    public EventFullDto getById(@PathVariable Long eventId,
                                HttpServletRequest request) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        String formattedTimestamp = LocalDateTime.now().format(formatter);

        statsClient.add(EndpointHit
                .builder()
                .app("ewm-main-service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(formattedTimestamp)
                .build());

        return null;
    }
}
