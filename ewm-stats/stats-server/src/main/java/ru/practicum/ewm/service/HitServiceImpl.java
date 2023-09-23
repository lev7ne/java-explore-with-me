package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.InvalidRequestException;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.repository.EndpointHitRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final EndpointHitRepository endpointHitRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public EndpointHit add(EndpointHit endpointHit) {
        Hit hit = EndpointHitMapper.mapToHit(endpointHit);

        return EndpointHitMapper.mapToEndpointHit(endpointHitRepository.save(hit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> getAll(String startDate, String endDate, List<String> uris, Boolean unique) {
        LocalDateTime start = LocalDateTime.parse(decode(startDate), formatter);
        LocalDateTime end = LocalDateTime.parse(decode(endDate), formatter);

        if (start.isAfter(end)) {
            throw new InvalidRequestException("The dates of the range are specified incorrectly");
        }

        List<ViewStats> viewStats;

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                viewStats = endpointHitRepository.readStatsWithUniqueViews(start, end);
            } else {
                viewStats = endpointHitRepository.readAllStats(start, end);
            }
        } else {
            if (unique) {
                viewStats = endpointHitRepository.readStatsWithUrisAndUniqueViews(start, end, uris);
            } else {
                viewStats = endpointHitRepository.readStatsWithUris(start, end, uris);
            }
        }
        return viewStats;
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
