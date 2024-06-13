package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.EventParamDto;
import ru.practicum.ewm.event.dto.EventUpdateAdminDto;
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
    private final EventAdminService eventAdminService;

    @GetMapping("")
    public ResponseEntity<List<EventDto>> index(@RequestParam(required = false) LocalDateTime rangeStart,
                                                @RequestParam(required = false) LocalDateTime rangeEnd,
                                                @RequestParam(required = false) List<Long> users,
                                                @RequestParam(required = false) List<Event.State> states,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {

        var param = new EventParamDto(rangeStart, rangeEnd, users, states, categories);
        var pageable = PageRequest.of(from / size, size);

        var dtos = eventAdminService.index(param, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    @PatchMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventDto> update(@Valid @RequestBody EventUpdateAdminDto eventUpdateAdminDto,
                                           @PathVariable long id) {

        var dto = eventAdminService.update(id, eventUpdateAdminDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }

}
