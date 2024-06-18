package ru.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.dto.EndpointHitCreateDto;
import ru.ewm.dto.EndpointHitDto;
import ru.ewm.dto.ViewStats;
import ru.ewm.mapper.EndpointHitMapper;
import ru.ewm.repository.EndpointHitRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;
    private final EndpointHitMapper endpointHitMapper;

    @Override
    @Transactional
    public EndpointHitDto create(EndpointHitCreateDto endpointHitCreateDto) {
        var hit = endpointHitMapper.toEntity(endpointHitCreateDto);
        hit = endpointHitRepository.save(hit);

        var dto = endpointHitMapper.toDto(hit);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> index(String StringStart, String StringEnd, List<String> uris, boolean unique) {

        LocalDateTime start = decode(StringStart);
        LocalDateTime end = decode(StringEnd);

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

    private LocalDateTime decode(String value) {
        return LocalDateTime.parse(URLDecoder.decode(value, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
