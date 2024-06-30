package ru.ewm.stat.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.ewm.event.mapper.EventContext;

import java.util.List;
import java.util.Map;

public interface StatsService {
    void addView(HttpServletRequest request);

    Map<Long, Long> getConfirmedRequests(List<Long> ids);

    Map<Long, Long> getViews(List<Long> ids);

    EventContext createEventContext(List<Long> ids);
}
