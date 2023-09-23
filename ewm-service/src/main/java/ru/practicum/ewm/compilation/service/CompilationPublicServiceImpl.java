package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.util.helper.ObjectFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationPublicServiceImpl implements CompilationPublicService {
    private final CompilationRepository compilationRepository;

    /**
     * Endpoint: GET "/compilations"
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
                .map(CompilationMapper::toCompilationDtoFromCompilation)
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
        return CompilationMapper.toCompilationDtoFromCompilation(ObjectFinder.findCompilationById(compilationRepository, compId));
    }
}
