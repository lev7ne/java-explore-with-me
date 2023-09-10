package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.repository.EndpointHitRepository;
import ru.practicum.ewm.mapper.EndpointHitDtoMapper;

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
        List<ViewStats> viewStats;

        if (uris == null && !unique) {
            viewStats = endpointHitRepository.readAllStats(start, end);
        } else if (!unique) {
            viewStats = endpointHitRepository.readStatsWithUris(start, end, uris);
        } else if (uris == null && unique) {
            viewStats = endpointHitRepository.readStatsWithUniqueViews(start, end);
        } else {
            viewStats = endpointHitRepository.readStatsWithUrisAndUniqueViews(start, end, uris);
        }

        return viewStats;
    }
}
