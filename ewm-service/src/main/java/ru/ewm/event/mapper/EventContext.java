package ru.ewm.event.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class EventContext {
    private Map<Long, Long> confirmedRequests;
    private Map<Long, Long> views;
}
