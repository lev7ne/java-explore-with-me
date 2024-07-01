package ru.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventParamDto;
import ru.ewm.event.dto.EventUpdateAdminDto;
import ru.ewm.event.model.Event;
import ru.ewm.event.service.EventAdminService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.ewm.util.configuration.JacksonConfig.DATE_TIME_FORMAT;


@Slf4j
@RestController
@RequestMapping(value = "/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventAdminService eventAdminService;

    @GetMapping("")
    public ResponseEntity<List<EventDto>> index(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(required = false) List<Event.State> states,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {

        log.info("Получен GET-запрос (admin) поиска с параметрами: " +
                        "users={}, categories={}, rangeStart={}, rangeEnd={}, states={}, from={}, size={}",
                users, categories, rangeStart, rangeEnd, states, from, size);

        var param = EventParamDto.builder()
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .users(users)
                .states(states)
                .categories(categories)
                .build();

        var pageable = PageRequest.of(from / size, size);

        var dtos = eventAdminService.index(param, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<EventDto> update(@Valid @RequestBody EventUpdateAdminDto updateDto,
                                           @PathVariable long id) {

        log.info("Получен PATCH-запрос (admin) на обновление события администратором: " +
                "eventId={}, eventUpdateAdminDto={}", id, updateDto);

        var dto = eventAdminService.update(id, updateDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }
}
