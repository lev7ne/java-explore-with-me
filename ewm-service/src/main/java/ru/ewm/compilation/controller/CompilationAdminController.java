package ru.ewm.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ewm.compilation.dto.CompilationCreateDto;
import ru.ewm.compilation.dto.CompilationDto;
import ru.ewm.compilation.dto.CompilationUpdateDto;
import ru.ewm.compilation.service.CompilationAdminService;


@RestController
@RequestMapping(value = "/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationAdminService compilationAdminService;

    @PostMapping
    public ResponseEntity<CompilationDto> create(@Valid @RequestBody CompilationCreateDto createDto) {

        var dto = compilationAdminService.create(createDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dto);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        compilationAdminService.delete(id);
    }

    @PatchMapping(value = "/{compId}")
    public ResponseEntity<CompilationDto> update(@PathVariable long id,
                                                 @Valid @RequestBody CompilationUpdateDto updateDto) {

        var dto = compilationAdminService.update(id, updateDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }
}
