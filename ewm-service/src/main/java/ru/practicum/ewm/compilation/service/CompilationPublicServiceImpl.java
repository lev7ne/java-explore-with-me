package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.util.exception.NotFoundException;
import ru.practicum.ewm.util.helper.ObjectCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final CompilationMapper compilationMapper;
    private final StatsClient statsClient;

    /**
     * Endpoint: GET "/compilations"
     *
     * @param pinned
     * @param pageable
     * @return List<CompilationDto>
     */
    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(boolean pinned, Pageable pageable) {
        List<Compilation> compilations;

        if (pinned) {
            compilations = compilationRepository.getAllByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }

        if (compilations.isEmpty()) {
            return new ArrayList<>();
        }

        return compilations.stream()
                .map(compilation -> {
                    List<Long> eventIds = compilation.getEvents().stream()
                            .map(Event::getId)
                            .collect(Collectors.toList());

                    Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
                    Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

                    List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                            .map(eventMapper::mapShort)
                            .map(eventShortDto -> {
                                eventShortDto.setConfirmedRequests(confirmedRequests.get(eventShortDto.getId()));
                                eventShortDto.setViews(countViews.get(eventShortDto.getId()));
                                return eventShortDto;
                            })
                            .collect(Collectors.toList());
                    CompilationDto compilationDto = compilationMapper.map(compilation);
                    compilationDto.setEvents(eventShortDtos);
                    return compilationDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Endpoint: GET "/compilations/{compId}"
     *
     * @param id
     * @return CompilationDto
     */
    @Override
    @Transactional(readOnly = true)
    public CompilationDto show(long id) {

        var compilation = compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + id + " was not found"));


        List<Long> eventIds = compilation.getEvents().stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

        var dto = compilationMapper.map(compilation);

        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(eventMapper::mapShort)
                .peek(eventShortDto -> {
                    eventShortDto.setConfirmedRequests(confirmedRequests.get(eventShortDto.getId()));
                    eventShortDto.setViews(countViews.get(eventShortDto.getId()));
                })
                .collect(Collectors.toList());

        dto.setEvents(eventShortDtos);

        return dto;
    }
}
