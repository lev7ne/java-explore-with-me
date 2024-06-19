package ru.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.compilation.dto.CompilationCreateDto;
import ru.ewm.compilation.dto.CompilationDto;
import ru.ewm.compilation.dto.CompilationUpdateDto;
import ru.ewm.compilation.mapper.CompilationMapper;
import ru.ewm.compilation.repository.CompilationRepository;
import ru.ewm.event.dto.EventShortDto;
import ru.ewm.event.mapper.EventMapper;
import ru.ewm.event.model.Event;
import ru.ewm.event.repository.EventRepository;
import ru.ewm.request.repository.RequestRepository;
import ru.ewm.util.exception.NotFoundException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final CompilationRepository compilationRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    /**
     *
     */
    @Override
    @Transactional
    public CompilationDto create(CompilationCreateDto createDto) {
        var compilation = compilationMapper.map(createDto);
        var ids = createDto.getEvents();

        if (ids.isEmpty()) {
            compilation = compilationRepository.save(compilation);
            return compilationMapper.map(compilation);
        }

        compilation = compilation.withEvents(eventRepository.findByIdIn(ids));
        compilation = compilationRepository.save(compilation);

//        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(ids, requestRepository);
//        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(ids, statsClient);

        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(eventMapper::mapShort)
                .peek(eventShortDto -> {
//                    eventShortDto.setViews(countViews.get(eventShortDto.getId()));
//                    eventShortDto.setConfirmedRequests(confirmedRequests.get(eventShortDto.getId()));
                })
                .toList();

        var dto = compilationMapper.map(compilation);
        dto.setEvents(eventShortDtos);

        return dto;
    }

    /**
     *
     */
    @Override
    @Transactional
    public CompilationDto update(long id, CompilationUpdateDto updateDto) {
        var compilation = compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + id + " was not found"));

        compilationMapper.update(updateDto, compilation);
        List<Event> events = compilation.getEvents();

        if (events.isEmpty()) {
            compilation = compilationRepository.save(compilation);
            return compilationMapper.map(compilation);
        }

        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();

        compilation = compilation.withEvents(eventRepository.findByIdIn(ids));
        compilation = compilationRepository.save(compilation);


//        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(ids, requestRepository);
//        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(ids, statsClient);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(eventMapper::mapShort)
                .peek(eventShortDto -> {
//                    eventShortDto.setViews(countViews.getOrDefault(eventShortDto.getId(), 0L));
//                    eventShortDto.setConfirmedRequests(confirmedRequests.getOrDefault(eventShortDto.getId(), 0L));
                })
                .toList();

        var dto = compilationMapper.map(compilation);
        dto.setEvents(eventShortDtos);

        return dto;
    }

    /**
     *
     */
    @Override
    @Transactional
    public void delete(long id) {
        compilationRepository.deleteById(id);
    }
}
