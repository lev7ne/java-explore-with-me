package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.compilation.dto.CompilationDto;

import java.util.List;

public interface CompilationPublicService {
    List<CompilationDto> getAll(Boolean pinned, Pageable pageable);

    CompilationDto getById(Long compId);
}
