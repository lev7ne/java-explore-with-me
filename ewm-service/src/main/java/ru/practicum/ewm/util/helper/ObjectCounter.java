package ru.practicum.ewm.util.helper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class ObjectCounter {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * this method finds the number of views for one eventId
     *
     * @param eventId
     * @param statsClient
     * @return long countViewsById
     */

    public long countViewsById(Long eventId, StatsClient statsClient) {
        List<ViewStats> stats = statsClient.getAll(
                LocalDateTime.now().minusYears(100).format(formatter),
                LocalDateTime.now().plusHours(1).format(formatter),
                List.of("/events/" + eventId),
                true
        );

        if (stats.isEmpty()) {
            return 0L;
        }

        return stats.size();
    }

    /**
     * this method finds the number of views for List<Long> eventIds
     *
     * @param eventIds
     * @param statsClient
     * @return Map<Long, Long> countViewsByIds
     */
    public Map<Long, Long> countViewsByIds(List<Long> eventIds, StatsClient statsClient) {
        List<String> uris = eventIds.stream()
                .map(eventId -> "/events/" + eventId)
                .collect(Collectors.toList());

        List<ViewStats> stats = statsClient.getAll(
                LocalDateTime.now().minusYears(100).format(formatter),
                LocalDateTime.now().plusHours(1).format(formatter),
                uris,
                true
        );

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> extractEventId(stat.getUri()),
                        ViewStats::getHits,
                        Long::sum
                ));
    }

    /**
     * this is a helper method that extracts the eventId from the uri
     *
     * @param uri
     * @return long eventId
     */
    private long extractEventId(String uri) {
        String[] parts = uri.split("/");
        return Long.parseLong(parts[2]);
    }

    /**
     *
     * @param eventIds
     * @param repository
     * @return Map<Long, Long> countConfirmedRequestByIds
     */
    public Map<Long, Long> countConfirmedRequestByIds(List<Long> eventIds, RequestRepository repository) {
        List<Request> confirmedRequests = repository.getAllByRequestStatusAndEvent_IdIn(Request.RequestStatus.CONFIRMED, eventIds);
        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId(), Collectors.counting()));
    }
}
