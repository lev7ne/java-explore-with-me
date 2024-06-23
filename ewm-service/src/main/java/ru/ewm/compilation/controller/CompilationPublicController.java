package ru.ewm.compilation.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ewm.compilation.dto.CompilationDto;
import ru.ewm.compilation.service.CompilationPublicService;

import java.util.List;


@RestController
@RequestMapping(value = "/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {
    private final CompilationPublicService compilationPublicService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> index(@RequestParam(required = false) boolean pinned,
                                                      @RequestParam(defaultValue = "0") @Min(0) int from,
                                                      @RequestParam(defaultValue = "10") @Min(1) int size) {

        var pageable = PageRequest.of(from / size, size);
        List<CompilationDto> dtos = compilationPublicService.index(pinned, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CompilationDto> show(@PathVariable long id) {
        var dto = compilationPublicService.show(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }
}
