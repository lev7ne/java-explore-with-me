package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.service.HitServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class EndpointHitController {
    private final HitServiceImpl hitService;
    @PostMapping(path = "/hit")
    public ResponseEntity<String> add(@RequestBody EndpointHit endpointHit) {
        hitService.add(endpointHit);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/stats")
    public List<ViewStats> getAll(@RequestParam LocalDateTime start,
                                  @RequestParam LocalDateTime end,
                                  @RequestParam String uris,
                                  @RequestParam Boolean unique) {
        return null;
    }
}