package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EndpointHitMapper {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Hit mapToHit(EndpointHit endpointHit) {
        return new Hit(
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                LocalDateTime.parse(endpointHit.getTimestamp(), formatter)
        );
    }

    public EndpointHit mapToEndpointHit(Hit hit) {
        return new EndpointHit(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimestamp().format(formatter));
    }
}
