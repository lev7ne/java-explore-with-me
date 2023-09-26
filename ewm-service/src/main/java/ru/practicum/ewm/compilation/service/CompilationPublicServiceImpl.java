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
import ru.practicum.ewm.util.helper.ObjectCounter;
import ru.practicum.ewm.util.helper.ObjectFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
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
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        List<Compilation> compilations;

        if (pinned != null) {
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
                            .map(EventMapper::toEventShortDtoFromEvent)
                            .map(eventShortDto -> {
                                eventShortDto.setConfirmedRequests(confirmedRequests.get(eventShortDto.getId()));
                                eventShortDto.setViews(countViews.get(eventShortDto.getId()));
                                return eventShortDto;
                            })
                            .collect(Collectors.toList());
                    CompilationDto compilationDto = CompilationMapper.toCompilationDtoFromCompilation(compilation);
                    compilationDto.setEvents(eventShortDtos);
                    return compilationDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Endpoint: GET "/compilations/{compId}"
     *
     * @param compId
     * @return CompilationDto
     */
    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compId) {
        Compilation compilation = ObjectFinder.findCompilationById(compilationRepository, compId);
        List<Long> eventIds = compilation.getEvents().stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

        CompilationDto compilationDto = CompilationMapper.toCompilationDtoFromCompilation(compilation);

        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(EventMapper::toEventShortDtoFromEvent)
                .peek(eventShortDto -> {
                    eventShortDto.setConfirmedRequests(confirmedRequests.get(eventShortDto.getId()));
                    eventShortDto.setViews(countViews.get(eventShortDto.getId()));
                })
                .collect(Collectors.toList());

        compilationDto.setEvents(eventShortDtos);

        return compilationDto;
    }
}
