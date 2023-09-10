package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.Hit;

@UtilityClass
public class EndpointHitDtoMapper {
    public Hit mapToHit(EndpointHit endpointHit) {
        return new Hit(
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
    }
}
