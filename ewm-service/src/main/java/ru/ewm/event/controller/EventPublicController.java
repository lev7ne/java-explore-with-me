package ru.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventParamDto;
import ru.ewm.event.dto.EventShortDto;
import ru.ewm.event.model.Event;
import ru.ewm.event.service.EventPublicService;
import ru.ewm.util.exception.InvalidRequestException;

import java.time.LocalDateTime;
import java.util.List;

import static ru.ewm.util.configuration.JacksonConfig.DATE_TIME_FORMAT;


@RestController
@RequestMapping(value = "/events")
@RequiredArgsConstructor
public class EventPublicController {
    private final EventPublicService eventPublicService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<EventDto> show(@PathVariable long id,
                                         HttpServletRequest request) {

        var dto = eventPublicService.show(id, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> index(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) Event.Sort sort,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            HttpServletRequest request
    ) {

        Pageable pageable;
        Sort sortOrder = Sort.unsorted();

        if (sort != null) {
            sortOrder = switch (sort) {
                case EVENT_DATE -> Sort.by("eventDate");
                case VIEWS -> Sort.by("views");
                default -> throw new InvalidRequestException("Incorrect sorting");
            };
        }

        pageable = PageRequest.of(from / size, size, sortOrder);

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new InvalidRequestException("The dates of the range are specified incorrectly");
        }

        var param = EventParamDto.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .build();

        List<EventShortDto> dtos = eventPublicService.index(param, pageable, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }
}
