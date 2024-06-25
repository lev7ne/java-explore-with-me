package ru.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ewm.dto.EndpointHitCreateDto;
import ru.ewm.dto.EndpointHitDto;
import ru.ewm.dto.ViewStats;
import ru.ewm.service.EndpointHitServiceImpl;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class EndpointHitController {
    private final EndpointHitServiceImpl endpointHitService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> create(@Valid @RequestBody EndpointHitCreateDto createDto) {

        log.info("Получен POST-запрос для создания EndpointHit: {}", createDto);

        try {
            EndpointHitDto dto = endpointHitService.create(createDto);
            log.info("Успешно создан EndpointHit: {}", dto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(dto);
        } catch (Exception e) {
            log.error("Ошибка при создании EndpointHit", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> index(@RequestParam(required = false) String start,
                                                 @RequestParam(required = false) String end,
                                                 @RequestParam(required = false) List<String> uris,
                                                 @RequestParam(required = false, defaultValue = "false") boolean unique) {

        log.info("Получен GET-запрос для статистики с параметрами: start={}, end={}, uris={}, unique={}",
                start, end, uris, unique);

        try {
            List<ViewStats> viewStats = endpointHitService.index(start, end, uris, unique);
            log.info("Успешно получена статистика: {}", viewStats);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(viewStats);

        } catch (Exception e) {
            log.error("Ошибка при получении статистики", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

}