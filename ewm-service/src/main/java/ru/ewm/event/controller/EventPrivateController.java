package ru.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ewm.event.dto.EventCreateDto;
import ru.ewm.event.dto.EventDto;
import ru.ewm.event.dto.EventShortDto;
import ru.ewm.event.dto.EventUpdateUserDto;
import ru.ewm.event.service.EventPrivateService;
import ru.ewm.request.dto.ParticipationRequestDto;
import ru.ewm.request.dto.RequestDto;
import ru.ewm.request.dto.RequestUpdateDto;

import java.util.List;


@Slf4j
@RestController
@RequestMapping(value = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventPrivateService eventPrivateService;

    @PostMapping("")
    public ResponseEntity<EventDto> create(@Valid @RequestBody EventCreateDto eventCreateDto,
                                           @PathVariable long userId) {

        var dto = eventPrivateService.create(eventCreateDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    @GetMapping(value = "/{eventId}")
    public ResponseEntity<EventDto> show(@PathVariable long userId,
                                         @PathVariable long eventId) {

        //TODO: т.к. для получения сущности event не требуется userId,
        // в метод дальше его не передаём, параметр понадобиться позже в Security

        var dto = eventPrivateService.show(eventId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }

    @PatchMapping(value = "/{eventId}")
    public ResponseEntity<EventDto> update(@Valid @RequestBody EventUpdateUserDto userDto,
                                           @PathVariable long userId,
                                           @PathVariable long eventId) {

        //TODO: т.к. для обновления сущности event не требуется userId,
        // в метод дальше его не передаём, параметр понадобиться позже в Security

        var dto = eventPrivateService.update(userDto, eventId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> index(@PathVariable long userId,
                                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(defaultValue = "10") @Min(1) Integer size) {

        var pageable = PageRequest.of(from / size, size);
        List<EventShortDto> dtos = eventPrivateService.index(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    @GetMapping(value = "/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> index(@PathVariable long userId,
                                                               @PathVariable long eventId) {

        var dtos = eventPrivateService.index(userId, eventId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    @PatchMapping(value = {"/{eventId}/requests", "/{eventId}/requests/"})
    public ResponseEntity<RequestDto> update(@PathVariable long userId,
                                             @PathVariable long eventId,
                                             @RequestBody RequestUpdateDto updateDto) {

        log.info("Получен PATCH-запрос обновления списка событий: " +
                "userId={}, eventId={}, requestUpdateDto={}", userId, eventId, updateDto);

        //TODO: т.к. для обновления сущности event не требуется userId,
        // в метод дальше его не передаём, параметр понадобиться позже в Security

        var dto = eventPrivateService.updateStatusRequests(updateDto, eventId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }
}
