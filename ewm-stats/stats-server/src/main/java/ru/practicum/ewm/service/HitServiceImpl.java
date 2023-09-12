package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.mapper.EndpointHitDtoMapper;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HitServiceImpl implements HitService {
    private final EndpointHitRepository endpointHitRepository;

    @Autowired
    public HitServiceImpl(EndpointHitRepository endpointHitRepository) {
        this.endpointHitRepository = endpointHitRepository;
    }

    @Override
    public void add(EndpointHit endpointHit) {
        endpointHitRepository.save(EndpointHitDtoMapper.mapToHit(endpointHit));
    }

    @Override
    public List<ViewStats> getAll(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (uris == null && !unique) {
            return endpointHitRepository.readAllStats(start, end);
        }
        if (!unique) {
            return endpointHitRepository.readStatsWithUris(start, end, uris);
        }
        if (uris == null && unique) {
            return endpointHitRepository.readStatsWithUniqueViews(start, end);
        }
        return endpointHitRepository.readStatsWithUrisAndUniqueViews(start, end, uris);
    }
}
