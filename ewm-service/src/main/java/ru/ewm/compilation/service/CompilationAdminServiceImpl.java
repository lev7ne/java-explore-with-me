package ru.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.compilation.dto.CompilationCreateDto;
import ru.ewm.compilation.dto.CompilationDto;
import ru.ewm.compilation.dto.CompilationUpdateDto;
import ru.ewm.compilation.mapper.CompilationMapper;
import ru.ewm.compilation.repository.CompilationRepository;
import ru.ewm.event.dto.EventShortDto;
import ru.ewm.event.mapper.EventContext;
import ru.ewm.event.mapper.EventMapper;
import ru.ewm.event.model.Event;
import ru.ewm.event.repository.EventRepository;
import ru.ewm.stat.service.StatsService;
import ru.ewm.util.exception.NotFoundException;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final StatsService statsService;

    /**
     *
     */
    @Override
    @Transactional
    public CompilationDto create(CompilationCreateDto createDto) {
        var compilation = compilationMapper.toEntity(createDto);
        compilation = compilationRepository.save(compilation);

        List<Long> ids = createDto.getEvents();

        List<Event> events = eventRepository.findByIdIn(ids);
        EventContext context = statsService.createEventContext(ids);

        List<EventShortDto> eventDtos = events.stream()
                .map(event -> eventMapper.toShortDto(event, context))
                .toList();

        var dto = compilationMapper.toDto(compilation);
        dto.setEvents(eventDtos);

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
        compilation = compilationRepository.save(compilation);

        List<Long> ids = updateDto.getEvents();
        if (ids.isEmpty()) {
            // Перезапись обновленной подборки, если в подборку НЕ добавлялись события
            compilation = compilationRepository.save(compilation);
            return compilationMapper.toDto(compilation);
        }

        List<Event> events = eventRepository.findByIdIn(ids);
        compilation.setEvents(events);
        // Перезапись обновленной подборки, если в подборку добавлялись события
        compilation = compilationRepository.save(compilation);

        EventContext context = statsService.createEventContext(ids);
        List<EventShortDto> eventDtos = events.stream()
                .map(event -> eventMapper.toShortDto(event, context))
                .toList();

        var dto = compilationMapper.toDto(compilation);
        dto.setEvents(eventDtos);

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
