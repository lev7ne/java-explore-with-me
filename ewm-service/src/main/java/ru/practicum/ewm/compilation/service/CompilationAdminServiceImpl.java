package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.compilation.dto.CompilationCreateDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.util.exception.NotFoundException;
import ru.practicum.ewm.util.helper.ObjectCounter;
import ru.practicum.ewm.util.helper.ObjectMerger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    /**
     * //FIXME: поправить описание метода
     *
     * @param createDto
     * @return CompilationDto
     */
    @Override
    @Transactional
    public CompilationDto create(CompilationCreateDto createDto) {
        var compilation = compilationMapper.map(createDto);
        var events = compilation.getEvents();

        if (events.isEmpty()) {
            compilation = compilationRepository.save(compilation);
            return compilationMapper.map(compilation);
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(eventMapper::mapShort)
                .peek(eventShortDto -> {
                    eventShortDto.setViews(countViews.get(eventShortDto.getId()));
                    eventShortDto.setConfirmedRequests(confirmedRequests.get(eventShortDto.getId()));
                })
                .collect(Collectors.toList());

        compilation = compilationRepository.save(compilation);
        var dto = compilationMapper.map(compilation);

        dto.setEvents(eventShortDtos);

        return dto;
    }

    /**
     * Endpoint: DELETE "/admin/compilations/{compId}"
     *
     * @param compId
     */
    @Override
    @Transactional
    public void delete(Long compId) {
        compilationRepository.deleteById(compId);
    }

    /**
     * Endpoint: PATCH "/admin/compilations/{compId}"
     *
     * @param compId
     * @param compilationUpdateDto
     * @return CompilationDto
     */
    @Override
    @Transactional
    public CompilationDto update(Long compId, CompilationUpdateDto compilationUpdateDto) {
        var compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + compId + " was not found"));

        ObjectMerger.copyProperties(compilationUpdateDto, compilation);

        List<Event> events = compilationUpdateDto.getEvents().isEmpty() ?
                new ArrayList<>() : eventRepository.getEventsByIdIn(compilationUpdateDto.getEvents());

        if (events.isEmpty()) {
            return compilationMapper.map(compilationRepository.save(compilation));
        }

        compilation.setEvents(events);

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(eventMapper::mapShort)
                .peek(eventShortDto -> {
                    eventShortDto.setViews(countViews.getOrDefault(eventShortDto.getId(), 0L));
                    eventShortDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventShortDto.getId(), 0L));
                })
                .collect(Collectors.toList());

        CompilationDto compilationDto = compilationMapper.map(compilation);
        compilationRepository.save(compilation);

        compilationDto.setEvents(eventShortDtos);

        return compilationDto;
    }
}
