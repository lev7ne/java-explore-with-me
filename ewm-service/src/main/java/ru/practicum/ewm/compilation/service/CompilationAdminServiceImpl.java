package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.util.helper.ObjectFinder;
import ru.practicum.ewm.util.helper.ObjectMerger;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminServiceImpl implements CompilationAdminService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

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
        compilation.setEvents(
                newCompilationDto.getEvents().isEmpty() ?
                        new ArrayList<>() : eventRepository.getEventsByIdIn(newCompilationDto.getEvents()));

        return CompilationMapper.toCompilationDtoFromCompilation(compilationRepository.save(compilation));
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

        updatedCompilation.setEvents(
                updateCompilationRequest.getEvents().isEmpty() ?
                        new ArrayList<>() : eventRepository.getEventsByIdIn(updateCompilationRequest.getEvents()));

        return CompilationMapper.toCompilationDtoFromCompilation(compilationRepository.save(updatedCompilation));
    }
}
