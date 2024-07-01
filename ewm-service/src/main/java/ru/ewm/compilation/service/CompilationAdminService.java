package ru.ewm.compilation.service;

import ru.ewm.compilation.dto.CompilationCreateDto;
import ru.ewm.compilation.dto.CompilationDto;
import ru.ewm.compilation.dto.CompilationUpdateDto;


public interface CompilationAdminService {
    CompilationDto create(CompilationCreateDto createDto);

    void delete(long id);

    CompilationDto update(long id, CompilationUpdateDto updateDto);
}
