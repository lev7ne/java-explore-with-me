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
import ru.ewm.exception.InvalidRequestException;

import java.time.LocalDateTime;
import java.util.List;

import static ru.ewm.configuration.JacksonConfig.DATE_TIME_FORMAT;


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
            @RequestParam(required = false) boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) Event.Sort sort,
            @RequestParam(defaultValue = "0") @Min(0) int from,

            //FIXME: Значение size по-умолчанию должно быть равным 10 - работает некорректно, понять почему?
            @RequestParam(defaultValue = "10") @Min(1) int size,
            HttpServletRequest request
    ) {

        Pageable pageable;

        if (sort != null) {
            pageable = switch (sort) {
                case EVENT_DATE -> PageRequest.of(from / size, size, Sort.by("eventDate"));
                case VIEWS -> PageRequest.of(from / size, size, Sort.by("views"));
                default -> throw new InvalidRequestException("Incorrect sorting");
            };
        } else {
            pageable = PageRequest.of(from / size, size);
        }

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
