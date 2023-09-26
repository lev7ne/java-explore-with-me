package ru.practicum.ewm.service;

import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.util.List;

public interface HitService {
    EndpointHit add(EndpointHit endpointHit);

    List<ViewStats> getAll(String startDate, String endDate, List<String> uris, Boolean unique);
}
