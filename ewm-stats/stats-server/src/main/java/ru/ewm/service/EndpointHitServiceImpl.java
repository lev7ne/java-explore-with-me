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

    /**
     *
     */
    @Override
    @Transactional
    public EndpointHitDto create(EndpointHitCreateDto createDto) {
        var hit = endpointHitMapper.toEntity(createDto);
        hit = endpointHitRepository.save(hit);

        var dto = endpointHitMapper.toDto(hit);

        return dto;
    }

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List<ViewStats> index(String start, String end, List<String> uris, boolean unique) {

        if (start != null && end != null) {
            LocalDateTime startTime = decode(start);
            LocalDateTime endTime = decode(end);
            return endpointHitRepository.readViewStats(startTime, endTime, uris, unique);
        }

        return endpointHitRepository.readViewStats(uris, unique);
    }

    /**
     * Расшифровка и преобразование строки (String) в дату и временя (LocalDateTime).
     *
     * @param value - значение строки даты
     * @return - преобразованное в LocalDateTime значение
     */
    private LocalDateTime decode(String value) {
        return LocalDateTime.parse(URLDecoder.decode(value, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
