package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationCreateDto;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateDto;
import ru.practicum.ewm.compilation.service.CompilationAdminService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationAdminService compilationAdminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@Valid @RequestBody CompilationCreateDto compilationCreateDto) {
        return compilationAdminService.create(compilationCreateDto);
    }

    @DeleteMapping(value = "/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        compilationAdminService.delete(compId);
    }

    @PatchMapping(value = "/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto update(@PathVariable Long compId,
                                 @Valid @RequestBody CompilationUpdateDto compilationUpdateDto) {
        return compilationAdminService.update(compId, compilationUpdateDto);
    }
}
