package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationCreateDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateDto;

public interface CompilationAdminService {
    CompilationDto create(CompilationCreateDto compilationCreateDto);

    void delete(Long compId);

    CompilationDto update(Long compId, CompilationUpdateDto compilationUpdateDto);
}
