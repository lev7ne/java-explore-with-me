package ru.ewm.service;

import ru.ewm.dto.EndpointHitCreateDto;
import ru.ewm.dto.EndpointHitDto;
import ru.ewm.dto.ViewStats;

import java.util.List;


public interface EndpointHitService {
    EndpointHitDto create(EndpointHitCreateDto endpointHitCreateDto);

    List<ViewStats> index(String start, String end, List<String> uris, boolean unique);
}
