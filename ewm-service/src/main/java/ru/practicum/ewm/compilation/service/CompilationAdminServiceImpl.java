package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.util.helper.ObjectCounter;
import ru.practicum.ewm.util.helper.ObjectFinder;
import ru.practicum.ewm.util.helper.ObjectMerger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    /**
     * Endpoint: POST "/admin/compilations"
     *
     * @param newCompilationDto
     * @return CompilationDto
     */
    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilationFromNewCompilationDto(newCompilationDto);

        List<Event> events = newCompilationDto.getEvents().isEmpty() ?
                new ArrayList<>() : eventRepository.getEventsByIdIn(newCompilationDto.getEvents());

        if (events.isEmpty()) {
            return CompilationMapper.toCompilationDtoFromCompilation(compilationRepository.save(compilation));
        }

        compilation.setEvents(events);

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(EventMapper::toEventShortDtoFromEvent)
                .peek(eventShortDto -> {
                    eventShortDto.setViews(countViews.get(eventShortDto.getId()));
                    eventShortDto.setConfirmedRequests(confirmedRequests.get(eventShortDto.getId()));
                })
                .collect(Collectors.toList());

        CompilationDto compilationDto = CompilationMapper.toCompilationDtoFromCompilation(compilationRepository.save(compilation));

        compilationDto.setEvents(eventShortDtos);

        return compilationDto;
    }

    /**
     * Endpoint: DELETE "/admin/compilations/{compId}"
     *
     * @param compId
     */
    @Override
    @Transactional
    public void delete(Long compId) {
        ObjectFinder.findCompilationById(compilationRepository, compId);
        compilationRepository.deleteById(compId);
    }

    /**
     * Endpoint: PATCH "/admin/compilations/{compId}"
     *
     * @param compId
     * @param updateCompilationRequest
     * @return CompilationDto
     */
    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation updatedCompilation = ObjectFinder.findCompilationById(compilationRepository, compId);

        ObjectMerger.copyProperties(updateCompilationRequest, updatedCompilation);

        List<Event> events = updateCompilationRequest.getEvents().isEmpty() ?
                new ArrayList<>() : eventRepository.getEventsByIdIn(updateCompilationRequest.getEvents());

        if (events.isEmpty()) {
            return CompilationMapper.toCompilationDtoFromCompilation(compilationRepository.save(updatedCompilation));
        }

        updatedCompilation.setEvents(events);

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(EventMapper::toEventShortDtoFromEvent)
                .peek(eventShortDto -> {
                    eventShortDto.setViews(countViews.getOrDefault(eventShortDto.getId(), 0L));
                    eventShortDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventShortDto.getId(), 0L));
                })
                .collect(Collectors.toList());

        CompilationDto compilationDto = CompilationMapper.toCompilationDtoFromCompilation(updatedCompilation);
        compilationRepository.save(updatedCompilation);

        compilationDto.setEvents(eventShortDtos);

        return compilationDto;
    }
}
