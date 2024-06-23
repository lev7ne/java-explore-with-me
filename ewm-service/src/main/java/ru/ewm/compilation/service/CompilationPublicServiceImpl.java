package ru.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ewm.compilation.dto.CompilationDto;
import ru.ewm.compilation.mapper.CompilationMapper;
import ru.ewm.compilation.model.Compilation;
import ru.ewm.compilation.repository.CompilationRepository;
import ru.ewm.event.dto.EventShortDto;
import ru.ewm.event.mapper.EventMapper;
import ru.ewm.event.model.Event;
import ru.ewm.util.exception.NotFoundException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationRepository compilationRepository;
    private final EventMapper eventMapper;
    private final CompilationMapper compilationMapper;

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> index(boolean pinned, Pageable pageable) {
        List<Compilation> compilations;

        if (pinned) {
            compilations = compilationRepository.getAllByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }

        if (compilations.isEmpty()) {
            return List.of();
        }

        List<CompilationDto> dtos = compilations.stream()
                .map(compilation -> {
                    List<Long> eventIds = compilation.getEvents().stream()
                            .map(Event::getId)
                            .toList();

//                    Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
//                    Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

                    List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                            .map(eventMapper::toShortDto)
                            .peek(eventShortDto -> {
//                                eventShortDto.setConfirmedRequests(confirmedRequests.get(eventShortDto.getId()));
//                                eventShortDto.setViews(countViews.get(eventShortDto.getId()));
                            })
                            .toList();
                    CompilationDto compilationDto = compilationMapper.toDto(compilation);
                    compilationDto.setEvents(eventShortDtos);
                    return compilationDto;
                })
                .toList();

        return dtos;
    }

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public CompilationDto show(long id) {
        var compilation = compilationRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + id + " was not found"));

        List<Long> eventIds = compilation.getEvents().stream()
                .map(Event::getId)
                .toList();

//        Map<Long, Long> confirmedRequests = ObjectCounter.countConfirmedRequestByIds(eventIds, requestRepository);
//        Map<Long, Long> countViews = ObjectCounter.countViewsByIds(eventIds, statsClient);

        var dto = compilationMapper.toDto(compilation);

        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(eventMapper::toShortDto)
                .peek(eventShortDto -> {
//                    eventShortDto.setConfirmedRequests(confirmedRequests.get(eventShortDto.getId()));
//                    eventShortDto.setViews(countViews.get(eventShortDto.getId()));
                })
                .toList();

//        dto.setEvents(eventShortDtos);

        return dto;
    }
}
