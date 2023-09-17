package ru.practicum.ewm.compilation.controller;

import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/admin/compilations")
public class CompilationAdminController {

    @PostMapping
    public CompilationDto create(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return null;
    }

    @DeleteMapping(value = "/{compId}")
    public void delete(@PathVariable Long compId) {
//        method body
    }

    @PatchMapping(value = "/{compId}")
    public CompilationDto update(@PathVariable Long compId,
                                 @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {

        return null;
    }
}
