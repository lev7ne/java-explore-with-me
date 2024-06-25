package ru.ewm.stat.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.StatsClient;
import ru.ewm.dto.EndpointHitCreateDto;
import ru.ewm.dto.ViewStats;
import ru.ewm.request.model.Request;
import ru.ewm.request.repository.RequestRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;

    /**
     *
     */
    @Override
    @Transactional
    public void addView(HttpServletRequest request) {
        var dto = EndpointHitCreateDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRequestURI())
                .build();

        statsClient.addView(dto);
    }

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> getViews(List<Long> ids) {
        List<String> uris = ids.stream()
                .map(id -> "/events/" + id)
                .toList();
        List<ViewStats> stats = statsClient.getViews(uris, true);

        Map<Long, Long> pair = stats.stream()
                .filter(stat -> isValidUri(stat.getUri()))
                .collect(Collectors.toMap(
                                stat -> extractId(stat.getUri()),
                                ViewStats::getHits,
                                Long::sum
                        )
                );

        return pair;
    }

    /**
     *
     */
    @Override
    public Map<Long, Long> getConfirmedRequests(List<Long> ids) {
        List<Request> confirmedRequests = requestRepository
                .findAllByRequestStatusAndEventIdIn(Request.RequestStatus.CONFIRMED, ids);
        Map<Long, Long> pair = confirmedRequests.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId(), Collectors.counting()));

        return pair;
    }

    /**
     *
     */
    private long extractId(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[2]);
    }

    /**
     *
     */
    private boolean isValidUri(String uri) {
        String[] parts = uri.split("/");
        return parts.length >= 3;
    }
}
