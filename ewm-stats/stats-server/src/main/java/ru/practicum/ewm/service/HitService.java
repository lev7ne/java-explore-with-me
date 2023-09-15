package ru.practicum.ewm.service;

import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    void add(EndpointHit endpointHit);

    List<ViewStats> getAll(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
