package ru.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.compilation.dto.CompilationCreateDto;
import ru.ewm.compilation.dto.CompilationDto;
import ru.ewm.compilation.dto.CompilationUpdateDto;
import ru.ewm.compilation.mapper.CompilationMapper;
import ru.ewm.compilation.repository.CompilationRepository;
import ru.ewm.event.mapper.EventMapper;
import ru.ewm.event.model.Event;
import ru.ewm.event.repository.EventRepository;
import ru.ewm.stat.service.StatsService;
import ru.ewm.util.exception.NotFoundException;

import java.util.List;
import java.util.Map;


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
        compilation = compilation.withEvents(eventRepository.findByIdIn(createDto.getEvents()));

        List<Event> events = compilation.getEvents();

        if (events.isEmpty()) {
            compilation = compilationRepository.save(compilation);
            return compilationMapper.toDto(compilation);
        }

        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();

        //TODO: разобраться как в подборку добавить ивенты с просмотрами/реквестами
        Map<Long, Long> requests = statsService.getConfirmedRequests(ids);
        Map<Long, Long> views = statsService.getViews(ids);

        var dto = compilationMapper.toDto(compilation);

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
            return compilationMapper.toDto(compilation);
        }

        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();

        compilation = compilation.withEvents(eventRepository.findByIdIn(ids));
        compilation = compilationRepository.save(compilation);


        //TODO: разобраться как в подборку добавить ивенты с просмотрами/реквестами
        Map<Long, Long> requests = statsService.getConfirmedRequests(ids);
        Map<Long, Long> views = statsService.getViews(ids);

        var dto = compilationMapper.toDto(compilation);

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
