package ru.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ewm.dto.EndpointHitCreateDto;
import ru.ewm.dto.EndpointHitDto;
import ru.ewm.dto.ViewStats;
import ru.ewm.service.EndpointHitServiceImpl;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class EndpointHitController {
    private final EndpointHitServiceImpl endpointHitService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> create(@Valid @RequestBody EndpointHitCreateDto createDto) {
        var dto = endpointHitService.create(createDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> index(@RequestParam(required = false) String start,
                                                 @RequestParam(required = false) String end,
                                                 @RequestParam(required = false) List<String> uris,
                                                 @RequestParam(required = false, defaultValue = "false") boolean unique) {

        List<ViewStats> viewStats = endpointHitService.index(start, end, uris, unique);

        return ResponseEntity.status(HttpStatus.OK)
                .body(viewStats);
    }

}