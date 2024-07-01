package ru.ewm.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.ewm.compilation.dto.CompilationDto;

import java.util.List;


public interface CompilationPublicService {
    List<CompilationDto> index(boolean pinned, Pageable pageable);

    CompilationDto show(long id);
}
