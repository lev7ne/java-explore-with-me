package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.exception.InvalidRequestException;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.service.HitServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HitController {
    private final HitServiceImpl hitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHit add(@RequestBody EndpointHit endpointHit) {
        return hitService.add(endpointHit);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStats> getAll(@RequestParam(required = false) String start,
                                  @RequestParam(required = false) String end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") Boolean unique) {

        if (start == null || end == null) {
            throw new InvalidRequestException("The start and end dates of the range cannot be empty");
        }

        return hitService.getAll(start, end, uris, unique);
    }
}